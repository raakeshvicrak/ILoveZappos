package com.zappos.raakeshpremkumar.ilovezappos.rest;

import com.zappos.raakeshpremkumar.ilovezappos.model.Products;

import java.util.ArrayList;

/**
 * Created by raakeshpremkumar on 2/1/17.
 */

public interface ApiResultInterface {

    public void onResult(ArrayList<Products> products, String searchTerm);
}
