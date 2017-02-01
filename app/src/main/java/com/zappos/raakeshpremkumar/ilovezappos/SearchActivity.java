package com.zappos.raakeshpremkumar.ilovezappos;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zappos.raakeshpremkumar.ilovezappos.DB.DataBaseManager;
import com.zappos.raakeshpremkumar.ilovezappos.DB.DataBaseQuery;
import com.zappos.raakeshpremkumar.ilovezappos.ProductsRecyclerView.ProductsRecyclerViewAdapter;
import com.zappos.raakeshpremkumar.ilovezappos.Utils.NetworkUtil;
import com.zappos.raakeshpremkumar.ilovezappos.model.ProductAPIResponse;
import com.zappos.raakeshpremkumar.ilovezappos.model.Products;
import com.zappos.raakeshpremkumar.ilovezappos.rest.ApiResultInterface;
import com.zappos.raakeshpremkumar.ilovezappos.rest.ExecuteRestApiSearch;
import com.zappos.raakeshpremkumar.ilovezappos.rest.RetrofitAPIClient;
import com.zappos.raakeshpremkumar.ilovezappos.rest.RetrofitAPIInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity implements ApiResultInterface{

    private EditText searchEditText;
    private final static String API_KEY = "b743e26728e16b81da139182bb2094357c31d331";
    private View parentLayout;
    private RecyclerView products_recyclerView;
    private DataBaseManager databaseManager;
    private ImageView clearButton, voiceButton;
    private static boolean searchMade = false;
    private ProductsRecyclerViewAdapter productsRecyclerViewAdapter = null;
    private Toolbar toolbar;
    private int REQUEST_CODE = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchMade == true){
                    searchMade = false;
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    getSupportActionBar().setDisplayShowHomeEnabled(false);
                    loadProducts();
                }
                else{
                    SearchActivity.super.onBackPressed();
                }
            }
        });

        searchEditText = (EditText) findViewById(R.id.searchEditText);
        parentLayout = findViewById(R.id.rootview);
        products_recyclerView = (RecyclerView) findViewById(R.id.productsview);
        clearButton = (ImageView) findViewById(R.id.clearButton);
        voiceButton = (ImageView) findViewById(R.id.voiceButton);

        products_recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, 1));

        loadProducts();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        searchEditText.setOnEditorActionListener(new OnEditorActionListener());

        clearButton.setOnClickListener(new onClearActionListener());

        voiceButton.setOnClickListener(new onVoiceActionListener());

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0){
                    voiceButton.setVisibility(View.GONE);
                    clearButton.setVisibility(View.VISIBLE);
                }
                else{
                    voiceButton.setVisibility(View.VISIBLE);
                    clearButton.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onResult(ArrayList<Products> products, String searchTerm) {

        if (productsRecyclerViewAdapter == null){
            productsRecyclerViewAdapter = new ProductsRecyclerViewAdapter(SearchActivity.this, products, R.layout.products_list_item);
            products_recyclerView.setAdapter(productsRecyclerViewAdapter);
        }
        else{
            productsRecyclerViewAdapter.setProducts(products);
            productsRecyclerViewAdapter.notifyDataSetChanged();
        }

        updateOrInsertDb(products, searchTerm);

        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public class onVoiceActionListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice recognition Demo...");
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            // Populate the wordsList with the String values the recognition engine thought it heard
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            searchEditText.setText(matches.get(0));
            searchEditText.setSelection(matches.get(0).length());

            searchMade = true;
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            executeRestApiSearch(searchEditText.getText().toString());
        }
    }

    public void loadProducts(){

        ArrayList<Products> products_list = DataBaseManager.getInstance(SearchActivity.this).retrieveTablerows(DataBaseQuery.TABLE_PRODUCT_DETAILS,
                DataBaseQuery.VIEWED, new String[]{"true"});

        if (products_list.size() > 0){

            if (productsRecyclerViewAdapter == null){
                productsRecyclerViewAdapter = new ProductsRecyclerViewAdapter(SearchActivity.this, products_list, R.layout.products_list_item);
                products_recyclerView.setAdapter(productsRecyclerViewAdapter);
            }
            else{
                productsRecyclerViewAdapter.setProducts(products_list);
                productsRecyclerViewAdapter.notifyDataSetChanged();
            }

            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
        else{
            executeRestApiSearch("");
        }
    }

    @Override
    public void onBackPressed() {
        if (searchMade == true){
            searchMade = false;
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            loadProducts();
        }
        else{
            super.onBackPressed();
        }
    }

    private void executeRestApiSearch(String searchTerm){

        ExecuteRestApiSearch executeRestApiSearc = new ExecuteRestApiSearch(SearchActivity.this, parentLayout, 0);
        executeRestApiSearc.executeRestApiSearch(searchTerm);
    }

    private void updateOrInsertDb(ArrayList<Products> products, String searchTerm){
        for (Products product : products){
            ContentValues contentValues = new ContentValues();
            contentValues.put(DataBaseQuery.PRODUCT_ID, product.getProductId());
            contentValues.put(DataBaseQuery.BRAND_NAME, product.getBrandName());
            contentValues.put(DataBaseQuery.COLOR_ID, product.getColorId());
            contentValues.put(DataBaseQuery.ORIGINAL_PRICE, product.getOriginalPrice());
            contentValues.put(DataBaseQuery.PERCENT_OFF, product.getPercentOff());
            contentValues.put(DataBaseQuery.PRICE, product.getPrice());
            contentValues.put(DataBaseQuery.PRODUCT_NAME, product.getProductName());
            contentValues.put(DataBaseQuery.PRODUCT_URL, product.getProductUrl());
            contentValues.put(DataBaseQuery.STYLE_ID, product.getStyleId());
            contentValues.put(DataBaseQuery.THUMBNAIL_IMAGE_URL, product.getThumbnailImageUrl());
            contentValues.put(DataBaseQuery.SEARCH_TERM, searchTerm);
            contentValues.put(DataBaseQuery.VIEWED, "false");

            databaseManager.getInstance(SearchActivity.this).insertProductDetails(contentValues);
        }

    }

    /*
     * Block gets executed when the search button in keyboard is clicked.
     */
    class OnEditorActionListener implements TextView.OnEditorActionListener{

        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i == EditorInfo.IME_ACTION_SEARCH){
                searchMade = true;
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                executeRestApiSearch(searchEditText.getText().toString());
            }
            return false;
        }
    }

    class onClearActionListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            searchEditText.setText("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_share) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
