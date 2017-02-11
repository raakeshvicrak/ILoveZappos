package com.zappos.raakeshpremkumar.ilovezappos;

import android.content.ContentValues;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zappos.raakeshpremkumar.ilovezappos.DB.DataBaseManager;
import com.zappos.raakeshpremkumar.ilovezappos.DB.DataBaseQuery;
import com.zappos.raakeshpremkumar.ilovezappos.ProductsRecyclerView.DeleteCallBack;
import com.zappos.raakeshpremkumar.ilovezappos.ProductsRecyclerView.ShoppingCartRecyclerViewAdapter;
import com.zappos.raakeshpremkumar.ilovezappos.model.Products;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ShoppingCartActivity extends AppCompatActivity implements DeleteCallBack {

    private ShoppingCartRecyclerViewAdapter shoppingCartRecyclerViewAdapter;
    private RecyclerView cart;
    private DataBaseManager databaseManager;
    private TextView totalPrice, noitems;
    private Button checkout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShoppingCartActivity.super.onBackPressed();
            }
        });

        cart = (RecyclerView) findViewById(R.id.cart);
        totalPrice = (TextView) findViewById(R.id.totalPrice);
        checkout = (Button) findViewById(R.id.checkout);
        noitems = (TextView) findViewById(R.id.noitems);

        setAdapter();

    }

    private void setAdapter(){
        ArrayList<Products> products_list = DataBaseManager.getInstance(ShoppingCartActivity.this).retrieveTablerows(DataBaseQuery.TABLE_PRODUCT_DETAILS,
                DataBaseQuery.ADDEDTOCART, new String[]{"true"});

        if (products_list.size() == 0){
            noitems.setVisibility(View.VISIBLE);
            cart.setVisibility(View.INVISIBLE);
        }
        else{
            noitems.setVisibility(View.INVISIBLE);
            cart.setVisibility(View.VISIBLE);
        }

        if (shoppingCartRecyclerViewAdapter == null){
            shoppingCartRecyclerViewAdapter = new ShoppingCartRecyclerViewAdapter(ShoppingCartActivity.this, products_list, R.layout.shoppingcart_list_item);
            cart.setAdapter(shoppingCartRecyclerViewAdapter);
        }
        else{
            shoppingCartRecyclerViewAdapter.setProducts(products_list);
            shoppingCartRecyclerViewAdapter.notifyDataSetChanged();
        }

        double sum = 0;
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        for (Products products: products_list){
            sum += Float.parseFloat(products.getPrice().substring(1));
        }

        if(sum != 0){
            totalPrice.setText(decimalFormat.format(sum)+"");
            checkout.setVisibility(View.VISIBLE);
        }
        else{
            totalPrice.setText("0.00");
            checkout.setVisibility(View.GONE);
        }
    }

    @Override
    public void updateRefreshAdapter(String productID) {
        updateAddToCart("false", productID);
        setAdapter();
    }

    private void updateAddToCart(String updateValue, String productID){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseQuery.PRODUCT_ID, productID);
        contentValues.put(DataBaseQuery.ADDEDTOCART, updateValue);

        databaseManager.getInstance(ShoppingCartActivity.this).updateProductDetails(contentValues);
    }
}
