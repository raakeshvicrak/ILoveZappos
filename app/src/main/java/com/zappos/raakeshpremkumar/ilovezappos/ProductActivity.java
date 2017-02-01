package com.zappos.raakeshpremkumar.ilovezappos;

import android.app.Activity;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.zappos.raakeshpremkumar.ilovezappos.Binding.ProductPojo;
import com.zappos.raakeshpremkumar.ilovezappos.DB.DataBaseManager;
import com.zappos.raakeshpremkumar.ilovezappos.DB.DataBaseQuery;
import com.zappos.raakeshpremkumar.ilovezappos.ProductsRecyclerView.ProductsRecyclerViewAdapter;
import com.zappos.raakeshpremkumar.ilovezappos.ProductsRecyclerView.SimilarProductsRecyclerViewAdapter;
import com.zappos.raakeshpremkumar.ilovezappos.databinding.ContentProductBinding;
import com.zappos.raakeshpremkumar.ilovezappos.model.Products;
import com.zappos.raakeshpremkumar.ilovezappos.rest.ApiResultInterface;
import com.zappos.raakeshpremkumar.ilovezappos.rest.ExecuteRestApiSearch;

import java.util.ArrayList;

public class ProductActivity extends AppCompatActivity implements ApiResultInterface {

    private TextView priceBefore;
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

        Intent intent = getIntent();

        //get the url from deep linking
        Uri data = intent.getData();

        if(data == null){
            //getting the data from previous activity
            Bundle bundle = intent.getExtras();
            productPojo = (ProductPojo) bundle.getSerializable("product");

            Log.e("productpojo ", productPojo.getBrandName());
            if (contentProductBinding == null){
                Log.e("null cont", "null");
            }
            contentProductBinding.setProductPojo(productPojo);
        }
        else{
            Log.e("last segment ",data.getLastPathSegment());
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

        executeRestApiSearch(productPojo.getProductName());

        if(databaseManager != null){
            databaseManager.close();
        }

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                fab.clearAnimation();
                Animation animation = AnimationUtils.loadAnimation(ProductActivity.this, R.anim.pop_down);
                fab.startAnimation(animation);

                fab.show();

                if(addedToCart == false){
                    addedToCart = true;
                    fab.setImageResource(R.drawable.done);
                    //fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                    //fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                }
                else{
                    addedToCart = false;
                    fab.setImageResource(R.drawable.shoppingcart);
                }

                // animation of popping up.
                fab.clearAnimation();
                Animation animation1 = AnimationUtils.loadAnimation(ProductActivity.this, R.anim.pop_up);
                fab.startAnimation(animation1);

            }
        });

        fab.setImageResource(R.drawable.shoppingcart);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        Drawable yourdrawable = menu.getItem(0).getIcon(); // change 0 with 1,2 ...
        yourdrawable.mutate();
        yourdrawable.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {

            // code to share the product with friends.
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out this product on @Zappos http://zapp.me/"+productPojo.getProductId());
            sendIntent.setType("text/plain");
            startActivity(sendIntent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void executeRestApiSearch(String searchTerm){

        ExecuteRestApiSearch executeRestApiSearc = new ExecuteRestApiSearch(ProductActivity.this, rootView, productPojo.getProductId());
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

            databaseManager.getInstance(ProductActivity.this).insertProductDetails(contentValues);
        }

    }

    @Override
    public void onResult(ArrayList<Products> products, String searchTerm) {

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
