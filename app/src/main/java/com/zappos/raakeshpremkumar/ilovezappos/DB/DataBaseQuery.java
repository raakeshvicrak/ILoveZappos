package com.zappos.raakeshpremkumar.ilovezappos.DB;

/**
 * Created by raakeshpremkumar on 1/28/17.
 */

public class DataBaseQuery {

    public static final String TABLE_PRODUCT_DETAILS = "PRODUCTDETAILS";

    public static final String S_NO = "SNO";
    public static final String PRODUCT_ID = "PRODUCTID";
    public static final String BRAND_NAME = "BRANDNAME";
    public static final String THUMBNAIL_IMAGE_URL = "THUMBNAILIMAGEURL";
    public static final String ORIGINAL_PRICE = "ORIGINALPRICE";
    public static final String STYLE_ID = "STYLEID";
    public static final String COLOR_ID = "COLORID";
    public static final String PRICE  = "PRICE";
    public static final String PERCENT_OFF = "PERCENTOFF";
    public static final String PRODUCT_URL = "PRODUCTURL";
    public static final String PRODUCT_NAME = "PRODUCTNAME";
    public static final String SEARCH_TERM = "SEARCHTERM";
    public static final String VIEWED = "VIEWED";

    public static final String CREATE_TABLE_PRODUCT_DETAILS = "CREATE TABLE " + TABLE_PRODUCT_DETAILS + "(" + S_NO
            + " INTEGER PRIMARY KEY, " + PRODUCT_ID + " TEXT," + BRAND_NAME + " TEXT," + THUMBNAIL_IMAGE_URL + " TEXT,"
            + ORIGINAL_PRICE + " TEXT," + STYLE_ID + " TEXT," + COLOR_ID + " TEXT," + PRICE + " TEXT," + PERCENT_OFF + " TEXT,"
            + PRODUCT_URL + " TEXT," + PRODUCT_NAME + " TEXT," + SEARCH_TERM + " TEXT," + VIEWED + " TEXT)";

}
