package com.zappos.raakeshpremkumar.ilovezappos.Binding;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

/**
 * Created by raakeshpremkumar on 1/24/17.
 */

public class ProductImageBindingAdapter {

    @BindingAdapter({"bind:image_url"})
    public static void loadImage(final ImageView imageView, String url)
    {
        Glide.with(imageView.getContext()).load(url).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                imageView.setBackgroundResource(0);
                return false;
            }
        }).into(imageView);
    }

}
