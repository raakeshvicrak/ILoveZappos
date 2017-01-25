package com.zappos.raakeshpremkumar.ilovezappos.Binding;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by raakeshpremkumar on 1/24/17.
 */

public class ProductImageBindingAdapter {

    @BindingAdapter({"bind:image_url"})
    public static void loadImage(ImageView imageView, String url)
    {
        Glide.with(imageView.getContext()).load(url).into(imageView);
    }

}
