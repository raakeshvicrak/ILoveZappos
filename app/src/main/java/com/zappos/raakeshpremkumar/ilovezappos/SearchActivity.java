package com.zappos.raakeshpremkumar.ilovezappos;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.zappos.raakeshpremkumar.ilovezappos.DB.DataBaseManager;
import com.zappos.raakeshpremkumar.ilovezappos.DB.DataBaseQuery;
import com.zappos.raakeshpremkumar.ilovezappos.ProductsRecyclerView.ProductsRecyclerViewAdapter;

import com.zappos.raakeshpremkumar.ilovezappos.model.Products;
import com.zappos.raakeshpremkumar.ilovezappos.rest.ApiResultInterface;
import com.zappos.raakeshpremkumar.ilovezappos.rest.ExecuteRestApiSearch;

import java.util.ArrayList;

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
    private TextView recentlyViewed;
    private FrameLayout noInternetConnectivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchEditText = (EditText) findViewById(R.id.searchEditText);
        parentLayout = findViewById(R.id.rootview);
        products_recyclerView = (RecyclerView) findViewById(R.id.productsview);
        clearButton = (ImageView) findViewById(R.id.clearButton);
        voiceButton = (ImageView) findViewById(R.id.voiceButton);
        recentlyViewed = (TextView) findViewById(R.id.recentlyViewed);
        noInternetConnectivity = (FrameLayout) findViewById(R.id.noInternetConnectivity);

        /*
         * Perform appropriate action when the back arrow in the toolbar is clicked.
         */
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

        products_recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, 1));

        /*
         * Function Call to load the reccylerview with recently viewed items if any or from API
         */
        loadProducts();

        /*
         * Search Listener for the Search EditText.
         */
        searchEditText.setOnEditorActionListener(new OnEditorActionListener());

        /*
         * Listener when the Cross button is clicked to clear the EditText.
         */
        clearButton.setOnClickListener(new onClearActionListener());

        /*
         * Listener to invoke the Google Voice to enable voice based search
         */
        voiceButton.setOnClickListener(new onVoiceActionListener());

        /*
         * EditText textwatcher to toggle between the voice and cross ImageView appropriately
         */
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

    /*
     * Interface Callback once the API is completed executing.
     */
    @Override
    public void onResult(ArrayList<Products> products, String searchTerm) {

        if (products.size() == 0){
            noInternetConnectivity.setVisibility(View.VISIBLE);
            products_recyclerView.setVisibility(View.GONE);
        }
        else{
            noInternetConnectivity.setVisibility(View.GONE);
            products_recyclerView.setVisibility(View.VISIBLE);
        }

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

    /*
     * Listener that invoked the Google voice enabling user voice based search
     */
    public class onVoiceActionListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak Now");
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    /*
     * gets called once the user is done speaking, the user speech is got and added to the search EditText
     */
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

    /*
     * Function that loads the reccylerview with recently viewed items if any or from API
     */
    public void loadProducts(){

        ArrayList<Products> products_list = DataBaseManager.getInstance(SearchActivity.this).retrieveTablerows(DataBaseQuery.TABLE_PRODUCT_DETAILS,
                DataBaseQuery.VIEWED, new String[]{"true"});

        if (products_list.size() > 0){
            noInternetConnectivity.setVisibility(View.GONE);
            products_recyclerView.setVisibility(View.VISIBLE);
            recentlyViewed.setVisibility(View.VISIBLE);
            if (productsRecyclerViewAdapter == null){
                productsRecyclerViewAdapter = new ProductsRecyclerViewAdapter(SearchActivity.this, products_list, R.layout.products_list_item);
                products_recyclerView.setAdapter(productsRecyclerViewAdapter);
            }
            else{
                productsRecyclerViewAdapter.setProducts(products_list);
                productsRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
        else{
            recentlyViewed.setVisibility(View.GONE);
            executeRestApiSearch("");
        }
    }

    /*
     * Handling onBackPress to toggle to the main screen or close the app appropriately.
     */
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

    /*
     * Refresh the recyclerview recently viewed once the activity is resumed.
     */
    @Override
    protected void onResume() {
        if (searchMade == false) {
            loadProducts();
        }
        super.onResume();
    }

    /*
     * Call to the RetroFit API to get the product list based on searchterm.
     */
    private void executeRestApiSearch(String searchTerm){

        ExecuteRestApiSearch executeRestApiSearc = new ExecuteRestApiSearch(SearchActivity.this, parentLayout, 0);
        executeRestApiSearc.executeRestApiSearch(searchTerm);
    }

    /*
     * Update the database
     */
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
                recentlyViewed.setVisibility(View.GONE);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);

                executeRestApiSearch(searchEditText.getText().toString());
            }
            return false;
        }
    }

    /*
     * Clear the Search EditText when the cross icon is clicked.
     */
    class onClearActionListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            searchEditText.setText("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        Drawable drawablecart = menu.getItem(0).getIcon();
        drawablecart.mutate();
        drawablecart.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

       if(id == R.id.shopping_cart){

            // code to open the shopping cart.
            Intent intent = new Intent(SearchActivity.this, ShoppingCartActivity.class);
            startActivity(intent);

            return true;

        }

        return super.onOptionsItemSelected(item);
    }
}
