package com.zappos.raakeshpremkumar.ilovezappos.ProductsRecyclerView;

import android.app.Activity;
import android.content.Intent;
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
import com.zappos.raakeshpremkumar.ilovezappos.Binding.ProductPojo;
import com.zappos.raakeshpremkumar.ilovezappos.ProductActivity;
import com.zappos.raakeshpremkumar.ilovezappos.R;
import com.zappos.raakeshpremkumar.ilovezappos.model.Products;

import java.util.ArrayList;

/**
 * Created by raakeshpremkumar on 1/31/17.
 */

public class SimilarProductsRecyclerViewAdapter extends RecyclerView.Adapter<SimilarProductsRecyclerViewAdapter.SimilarProductsViewHolder> {

    private Activity activity;
    public static ArrayList<Products> products;
    private int rowLayout;

    public SimilarProductsRecyclerViewAdapter(Activity activity, ArrayList<Products> products, int rowLayout){
        this.activity = activity;
        this.products = products;
        this.rowLayout = rowLayout;
    }

    public static class SimilarProductsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView recentlyviewed_productImage;
        private TextView brandName, productName;
        private Activity activity;
        private LinearLayout recentlyViewed_parent_layout;

        public SimilarProductsViewHolder(View itemView, Activity activity) {
            super(itemView);

            this.activity = activity;
            recentlyviewed_productImage = (ImageView) itemView.findViewById(R.id.recentlyviewed_productImage);
            brandName = (TextView) itemView.findViewById(R.id.brandName);
            productName = (TextView) itemView.findViewById(R.id.productName);
            recentlyViewed_parent_layout = (LinearLayout) itemView.findViewById(R.id.recentlyViewed_parent_layout);

            recentlyViewed_parent_layout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            ProductPojo productPojo = new ProductPojo(products.get(getAdapterPosition()));

            Intent intent = new Intent(this.activity, ProductActivity.class);
            intent.putExtra("product", productPojo);

            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(activity, (View)recentlyviewed_productImage, "productImageTransition");
            this.activity.finish();

            this.activity.startActivity(intent, options.toBundle());
        }
    }

    public void setProducts(ArrayList<Products> products){
        this.products = products;
    }

    @Override
    public SimilarProductsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new SimilarProductsRecyclerViewAdapter.SimilarProductsViewHolder(view, activity);
    }

    @Override
    public void onBindViewHolder(final SimilarProductsViewHolder holder, int position) {
        Products product_object = products.get(position);

        //Glide.with(activity).load(product_object.getThumbnailImageUrl()).into(holder.recentlyviewed_productImage);

        Glide.with(activity).load(product_object.getThumbnailImageUrl()).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                holder.recentlyviewed_productImage.setBackgroundResource(0);
                return false;
            }
        }).into(holder.recentlyviewed_productImage);


        holder.brandName.setText(Html.fromHtml(product_object.getBrandName()));

        holder.productName.setText(Html.fromHtml(product_object.getProductName()));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

}
