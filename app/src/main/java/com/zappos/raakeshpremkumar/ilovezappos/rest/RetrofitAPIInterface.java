package com.zappos.raakeshpremkumar.ilovezappos.rest;

import com.zappos.raakeshpremkumar.ilovezappos.model.ProductAPIResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by raakeshpremkumar on 1/23/17.
 */

public interface RetrofitAPIInterface {
    @GET("Search?")
    Call<ProductAPIResponse> getTopSearchResults(@Query("term") String searchterm, @Query("key") String apiKey);
}
