package com.zappos.raakeshpremkumar.ilovezappos.model;

/**
 * Created by raakeshpremkumar on 1/23/17.
 */

/*
 * Plain Old Java Object Class for storing the information about the products.
 */

public class Products {

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

    public Products(){}

    public Products(String brandName, String thumbnailImageUrl, long productId, String originalPrice, long styleId, long colorId,
                    String price, String percentOff, String productUrl, String productName){
        this.brandName = brandName;
        this.thumbnailImageUrl = thumbnailImageUrl;
        this.productId = productId;
        this.originalPrice = originalPrice;
        this.styleId = styleId;
        this.colorId = colorId;
        this.price = price;
        this.percentOff = percentOff;
        this.productUrl = productUrl;
        this.productName = productName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
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
