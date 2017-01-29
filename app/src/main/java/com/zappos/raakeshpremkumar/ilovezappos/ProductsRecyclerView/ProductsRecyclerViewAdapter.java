package com.zappos.raakeshpremkumar.ilovezappos.ProductsRecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zappos.raakeshpremkumar.ilovezappos.Binding.ProductPojo;
import com.zappos.raakeshpremkumar.ilovezappos.ProductActivity;
import com.zappos.raakeshpremkumar.ilovezappos.R;
import com.zappos.raakeshpremkumar.ilovezappos.model.Products;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by raakeshpremkumar on 1/23/17.
 */

public class ProductsRecyclerViewAdapter extends RecyclerView.Adapter<ProductsRecyclerViewAdapter.ProductsViewHolder> {

    private Activity activity;
    public static ArrayList<Products> products;
    private int rowLayout;

    public static class ProductsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private LinearLayout productLayout;
        private TextView sale;
        private ImageView productImage;
        private TextView discount;
        private TextView brandName;
        private TextView productName;
        private TextView priceBefore;
        private TextView priceAfter;
        private Activity activity;

        public ProductsViewHolder(View view, Activity activity){
            super(view);
            productLayout = (LinearLayout) view.findViewById(R.id.productLayout);
            sale = (TextView) view.findViewById(R.id.sale);
            productImage = (ImageView) view.findViewById(R.id.productImage);
            discount = (TextView) view.findViewById(R.id.discount);
            brandName = (TextView) view.findViewById(R.id.brandName);
            productName = (TextView) view.findViewById(R.id.productName);
            priceBefore = (TextView) view.findViewById(R.id.priceBefore);
            priceAfter = (TextView) view.findViewById(R.id.priceAfter);
            this.activity = activity;

            productLayout.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            ProductPojo productPojo = new ProductPojo(products.get(getAdapterPosition()));

            Intent intent = new Intent(this.activity, ProductActivity.class);
            intent.putExtra("product", productPojo);

            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(activity, (View)productImage, "productImageTransition");


            this.activity.startActivity(intent, options.toBundle());
        }
    }

    public ProductsRecyclerViewAdapter(Activity activity, ArrayList<Products> products, int rowLayout){
        this.activity = activity;
        this.products = products;
        this.rowLayout = rowLayout;
    }

    @Override
    public ProductsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new ProductsViewHolder(view, activity);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBindViewHolder(ProductsViewHolder holder, int position) {

        Products product_object = products.get(position);

        // Check if the Product is on Sale:
        if (product_object.getOriginalPrice().toString().equals(product_object.getPrice())){
            holder.sale.setVisibility(View.INVISIBLE);

            holder.discount.setText("NEW!");
            holder.discount.setTextColor(activity.getResources().getColor(R.color.blue));

            holder.priceBefore.setVisibility(View.GONE);

            holder.priceAfter.setText(product_object.getPrice());
            holder.priceAfter.setTextColor(activity.getResources().getColor(R.color.green));
        }
        else{
            holder.sale.setVisibility(View.VISIBLE);

            holder.discount.setText(product_object.getPercentOff()+" OFF!");
            holder.discount.setTextColor(activity.getResources().getColor(R.color.red));

            holder.priceBefore.setText(product_object.getOriginalPrice());
            holder.priceBefore.setPaintFlags(holder.priceBefore.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            holder.priceAfter.setText(product_object.getPrice());
            holder.priceAfter.setTextColor(activity.getResources().getColor(R.color.red));
        }

        // Load the Image of the product.
        Glide.with(activity).load(product_object.getThumbnailImageUrl()).into(holder.productImage);

        holder.brandName.setText(Html.fromHtml(product_object.getBrandName()));

        holder.productName.setText(Html.fromHtml(product_object.getProductName()));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }


}
