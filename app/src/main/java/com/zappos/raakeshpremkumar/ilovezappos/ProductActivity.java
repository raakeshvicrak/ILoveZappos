package com.zappos.raakeshpremkumar.ilovezappos;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.annotationprocessor.ProcessDataBinding;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.zappos.raakeshpremkumar.ilovezappos.Binding.ProductPojo;
import com.zappos.raakeshpremkumar.ilovezappos.databinding.ContentProductBinding;

public class ProductActivity extends AppCompatActivity {

    private TextView priceBefore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_product);

        ContentProductBinding contentProductBinding = DataBindingUtil.setContentView(this, R.layout.content_product);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        priceBefore = (TextView) findViewById(R.id.priceBefore);
        priceBefore.setPaintFlags(priceBefore.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        ProductPojo productPojo = (ProductPojo) bundle.getSerializable("product");

        Log.e("productpojo ", productPojo.getBrandName());
        if (contentProductBinding == null){
            Log.e("null cont", "null");
        }
        contentProductBinding.setProductPojo(productPojo);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }

}
