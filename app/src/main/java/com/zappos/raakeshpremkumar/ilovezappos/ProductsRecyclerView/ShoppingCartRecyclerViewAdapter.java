package com.zappos.raakeshpremkumar.ilovezappos.ProductsRecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.zappos.raakeshpremkumar.ilovezappos.R;
import com.zappos.raakeshpremkumar.ilovezappos.model.Products;

import java.util.ArrayList;

/**
 * Created by raakeshpremkumar on 2/10/17.
 */

public class ShoppingCartRecyclerViewAdapter extends RecyclerView.Adapter<ShoppingCartRecyclerViewAdapter.ShoppingCartViewHolder> {

    private Activity activity;
    public static ArrayList<Products> products;
    private int rowLayout;

    /*
     * View Holder for each item in the Recyclerview.
     */
    public static class ShoppingCartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private LinearLayout productLayout;
        private ImageView productImage;
        private TextView discount;
        private TextView brandName;
        private TextView productName;
        private TextView priceBefore;
        private TextView priceAfter;
        private DeleteCallBack activity;
        private ImageView crossButton;
        private long productID;

        public ShoppingCartViewHolder(View view, Activity activity){
            super(view);
            productLayout = (LinearLayout) view.findViewById(R.id.productLayout);
            productImage = (ImageView) view.findViewById(R.id.productImage);
            discount = (TextView) view.findViewById(R.id.discount);
            brandName = (TextView) view.findViewById(R.id.brandName);
            productName = (TextView) view.findViewById(R.id.productName);
            priceBefore = (TextView) view.findViewById(R.id.priceBefore);
            priceAfter = (TextView) view.findViewById(R.id.priceAfter);
            crossButton = (ImageView) view.findViewById(R.id.crossButton);
            this.activity = (DeleteCallBack) activity;

            crossButton.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            this.activity.updateRefreshAdapter(String.valueOf(productID));
        }
    }

    public ShoppingCartRecyclerViewAdapter(Activity activity, ArrayList<Products> products, int rowLayout){
        this.activity = activity;
        this.products = products;
        this.rowLayout = rowLayout;
    }

    public void setProducts(ArrayList<Products> products){
        this.products = products;
    }

    @Override
    public ShoppingCartRecyclerViewAdapter.ShoppingCartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new ShoppingCartRecyclerViewAdapter.ShoppingCartViewHolder(view, activity);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBindViewHolder(final ShoppingCartRecyclerViewAdapter.ShoppingCartViewHolder holder, int position) {

        Products product_object = products.get(position);

        holder.productID = product_object.getProductId();

        // Check if the Product is on Sale:
        if (product_object.getOriginalPrice().toString().equals(product_object.getPrice())){

            holder.discount.setText("NEW!");
            holder.discount.setTextColor(activity.getResources().getColor(R.color.blue));

            holder.priceBefore.setVisibility(View.GONE);

            holder.priceAfter.setText(product_object.getPrice());
            holder.priceAfter.setTextColor(activity.getResources().getColor(R.color.green));
        }
        else{

            holder.discount.setText(product_object.getPercentOff()+" OFF!");
            holder.discount.setTextColor(activity.getResources().getColor(R.color.red));

            holder.priceBefore.setText(product_object.getOriginalPrice());
            holder.priceBefore.setPaintFlags(holder.priceBefore.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            holder.priceAfter.setText(product_object.getPrice());
            holder.priceAfter.setTextColor(activity.getResources().getColor(R.color.red));
        }

        // Load the Image of the product.
        Glide.with(activity).load(product_object.getThumbnailImageUrl()).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                holder.productImage.setBackgroundResource(0);
                return false;
            }
        }).into(holder.productImage);

        holder.brandName.setText(Html.fromHtml(product_object.getBrandName()));

        holder.productName.setText(Html.fromHtml(product_object.getProductName()));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

}
