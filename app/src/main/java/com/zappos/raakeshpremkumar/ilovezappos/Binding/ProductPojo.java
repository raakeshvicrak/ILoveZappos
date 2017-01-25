package com.zappos.raakeshpremkumar.ilovezappos.Binding;

import android.databinding.BaseObservable;

import com.zappos.raakeshpremkumar.ilovezappos.BR;
import com.zappos.raakeshpremkumar.ilovezappos.model.Products;

import java.io.Serializable;

/**
 * Created by raakeshpremkumar on 1/24/17.
 */

public class ProductPojo extends BaseObservable implements Serializable {

    private String brandName;
    private String thumbnailImageUrl;
    private long productId;
    private String originalPrice;
    private long styleId;
    private long colorId;
    private String price;
    private String percentOff;
    private String productUrl;
    private String productName;

    public ProductPojo(Products products){
        this.brandName = products.getBrandName();
        this.thumbnailImageUrl = products.getThumbnailImageUrl();
        this.productId = products.getProductId();
        this.originalPrice = products.getOriginalPrice();
        this.styleId = products.getStyleId();
        this.colorId = products.getColorId();
        this.price = products.getPrice();
        this.percentOff = products.getPercentOff();
        this.productUrl = products.getProductUrl();
        this.productName = products.getProductName();

        if (this.percentOff.equals("0%")){
            this.percentOff = "";
        }
        else{
            this.percentOff +=" OFF!";
        }

        this.productName.replace("uoo26","&");

        if (this.price.equals(this.originalPrice)){
            this.originalPrice = "";
        }
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
        notifyPropertyChanged(BR.productPojo);
    }

    public String getThumbnailImageUrl() {
        return thumbnailImageUrl;
    }

    public void setThumbnailImageUrl(String thumbnailImageUrl) {
        this.thumbnailImageUrl = thumbnailImageUrl;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public long getStyleId() {
        return styleId;
    }

    public void setStyleId(long styleId) {
        this.styleId = styleId;
    }

    public long getColorId() {
        return colorId;
    }

    public void setColorId(long colorId) {
        this.colorId = colorId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPercentOff() {
        return percentOff;
    }

    public void setPercentOff(String percentOff) {
        this.percentOff = percentOff;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
