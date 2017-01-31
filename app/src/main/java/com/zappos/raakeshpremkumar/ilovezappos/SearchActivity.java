package com.zappos.raakeshpremkumar.ilovezappos;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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
import com.zappos.raakeshpremkumar.ilovezappos.rest.RetrofitAPIClient;
import com.zappos.raakeshpremkumar.ilovezappos.rest.RetrofitAPIInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    private EditText searchEditText;
    private final static String API_KEY = "b743e26728e16b81da139182bb2094357c31d331";
    private View parentLayout;
    private RecyclerView products_recyclerView;
    private DataBaseManager databaseManager;
    private ImageView clearButton;
    private static boolean searchMade = false;
    private ProductsRecyclerViewAdapter productsRecyclerViewAdapter = null;
    private Toolbar toolbar;

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

    private void executeRestApiSearch(final String searchTerm){
        if (API_KEY.isEmpty()){
            Toast.makeText(SearchActivity.this, "API Key is empty!", Toast.LENGTH_LONG).show();
        }
        else{

            if (NetworkUtil.isNetworkAvailable(SearchActivity.this)){
                RetrofitAPIInterface apiInterface = RetrofitAPIClient.getClient().create(RetrofitAPIInterface.class);

                Call<ProductAPIResponse> call = apiInterface.getTopSearchResults(searchTerm, API_KEY);
                call.enqueue(new Callback<ProductAPIResponse>() {
                    @Override
                    public void onResponse(Call<ProductAPIResponse> call, Response<ProductAPIResponse> response) {
                        ArrayList<Products> products = response.body().getResults();
                        Toast.makeText(SearchActivity.this, "Number of products "+products.size(), Toast.LENGTH_LONG).show();
                        for (Products productstemp: products){
                            Log.e("response ", productstemp.getProductName());
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

                    @Override
                    public void onFailure(Call<ProductAPIResponse> call, Throwable t) {
                        Toast.makeText(SearchActivity.this, ""+t.toString(), Toast.LENGTH_LONG).show();
                    }
                });
            }
            else{
                Snackbar.make(parentLayout, "No Internet Connectivity!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                ArrayList<Products> products_list = DataBaseManager.getInstance(SearchActivity.this).retrieveTablerows(DataBaseQuery.TABLE_PRODUCT_DETAILS,
                        DataBaseQuery.SEARCH_TERM, new String[]{searchTerm});

                if (productsRecyclerViewAdapter == null){
                    productsRecyclerViewAdapter = new ProductsRecyclerViewAdapter(SearchActivity.this, products_list, R.layout.products_list_item);
                    products_recyclerView.setAdapter(productsRecyclerViewAdapter);
                }
                else{
                    productsRecyclerViewAdapter.setProducts(products_list);
                    productsRecyclerViewAdapter.notifyDataSetChanged();
                }

                updateOrInsertDb(products_list, searchTerm);

                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }

        }
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
