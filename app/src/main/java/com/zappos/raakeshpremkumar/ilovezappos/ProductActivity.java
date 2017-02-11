package com.zappos.raakeshpremkumar.ilovezappos;

import android.content.ContentValues;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.zappos.raakeshpremkumar.ilovezappos.Binding.ProductPojo;
import com.zappos.raakeshpremkumar.ilovezappos.DB.DataBaseManager;
import com.zappos.raakeshpremkumar.ilovezappos.DB.DataBaseQuery;
import com.zappos.raakeshpremkumar.ilovezappos.ProductsRecyclerView.SimilarProductsRecyclerViewAdapter;
import com.zappos.raakeshpremkumar.ilovezappos.databinding.ContentProductBinding;
import com.zappos.raakeshpremkumar.ilovezappos.model.Products;
import com.zappos.raakeshpremkumar.ilovezappos.rest.ApiResultInterface;
import com.zappos.raakeshpremkumar.ilovezappos.rest.ExecuteRestApiSearch;

import java.util.ArrayList;

public class ProductActivity extends AppCompatActivity implements ApiResultInterface {

    private TextView priceBefore, similaritemsparent;
    private FloatingActionButton fab;
    private boolean addedToCart = false;
    private ProductPojo productPojo;
    private DataBaseManager databaseManager;
    private RecyclerView similarProducts;
    private SimilarProductsRecyclerViewAdapter similarProductsRecyclerViewAdapter = null;
    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ContentProductBinding contentProductBinding = DataBindingUtil.setContentView(this, R.layout.content_product);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProductActivity.super.onBackPressed();
            }
        });

        priceBefore = (TextView) findViewById(R.id.priceBefore);
        priceBefore.setPaintFlags(priceBefore.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        similarProducts = (RecyclerView) findViewById(R.id.similarProducts);
        rootView = (View) findViewById(R.id.rootview);
        similaritemsparent = (TextView) findViewById(R.id.similaritemsparent);

        Intent intent = getIntent();

        //get the url from deep linking
        Uri data = intent.getData();

        if(data == null){
            //getting the data from previous activity
            Bundle bundle = intent.getExtras();
            productPojo = (ProductPojo) bundle.getSerializable("product");

            contentProductBinding.setProductPojo(productPojo);
        }
        else{
            ArrayList<Products> products_list = DataBaseManager.getInstance(ProductActivity.this).retrieveTablerows(DataBaseQuery.TABLE_PRODUCT_DETAILS,
                    DataBaseQuery.PRODUCT_ID, new String[]{data.getLastPathSegment()});
            if (products_list.size() > 0){
                productPojo = new ProductPojo(products_list.get(0));
                contentProductBinding.setProductPojo(productPojo);
            }
        }

        if(productPojo != null){
            ContentValues contentValues = new ContentValues();
            contentValues.put(DataBaseQuery.PRODUCT_ID, productPojo.getProductId());
            contentValues.put(DataBaseQuery.VIEWED, "true");
            databaseManager.getInstance(ProductActivity.this).updateProductDetails(contentValues);
        }

        /*
         * Execute this call to get the Similar items.
         */
        executeRestApiSearch(productPojo.getProductName());

        if(databaseManager != null){
            databaseManager.close();
        }

        fab = (FloatingActionButton) findViewById(R.id.fab);

        /*
         * Trigger Animation when FAB is clicked.
         */
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.clearAnimation();
                Animation animation = AnimationUtils.loadAnimation(ProductActivity.this, R.anim.pop_down);
                fab.startAnimation(animation);

                fab.show();

                if(addedToCart == false){
                    addedToCart = true;
                    fab.setImageResource(R.drawable.done);
                    updateAddToCart("true");
                }
                else{
                    addedToCart = false;
                    fab.setImageResource(R.drawable.shoppingcart);
                    updateAddToCart("false");
                }

                // animation of popping up.
                fab.clearAnimation();
                Animation animation1 = AnimationUtils.loadAnimation(ProductActivity.this, R.anim.pop_up);
                fab.startAnimation(animation1);

            }
        });

        fab.setImageResource(R.drawable.shoppingcart);

    }

    private void updateAddToCart(String updateValue){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseQuery.PRODUCT_ID, productPojo.getProductId());
        contentValues.put(DataBaseQuery.ADDEDTOCART, updateValue);

        databaseManager.getInstance(ProductActivity.this).updateProductDetails(contentValues);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_product, menu);

        Drawable drawable = menu.getItem(0).getIcon();
        drawable.mutate();
        drawable.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);

        Drawable drawablecart = menu.getItem(1).getIcon();
        drawablecart.mutate();
        drawablecart.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_share) {

            // code to share the product with friends.
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out this product on @Zappos http://zapp.me/"+productPojo.getProductId());
            sendIntent.setType("text/plain");
            startActivity(sendIntent);

            return true;
        }

        else if(id == R.id.shopping_cart){

            // code to open the shopping cart.
            Intent intent = new Intent(ProductActivity.this, ShoppingCartActivity.class);
            startActivity(intent);

            return true;

        }

        return super.onOptionsItemSelected(item);
    }


    /*
     * Call to the RetroFit API to get the similar items.
     */
    private void executeRestApiSearch(String searchTerm){
        ExecuteRestApiSearch executeRestApiSearc = new ExecuteRestApiSearch(ProductActivity.this, rootView, productPojo.getProductId());
        executeRestApiSearc.executeRestApiSearch(searchTerm);
    }

    /*
     * Database Update.
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

            databaseManager.getInstance(ProductActivity.this).insertProductDetails(contentValues);
        }

    }

    /*
     * Interface Callback once the API is completed executing.
     */
    @Override
    public void onResult(ArrayList<Products> products, String searchTerm) {

        if (products.size() > 0){
            similaritemsparent.setVisibility(View.VISIBLE);
        }

        if (similarProductsRecyclerViewAdapter == null){
            similarProductsRecyclerViewAdapter = new SimilarProductsRecyclerViewAdapter(ProductActivity.this, products, R.layout.recentlyviewed_list_item);
            similarProducts.setAdapter(similarProductsRecyclerViewAdapter);
        }
        else{
            similarProductsRecyclerViewAdapter.setProducts(products);
            similarProductsRecyclerViewAdapter.notifyDataSetChanged();
        }

        updateOrInsertDb(products, searchTerm);
    }
}
