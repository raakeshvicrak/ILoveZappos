package com.zappos.raakeshpremkumar.ilovezappos.rest;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by raakeshpremkumar on 1/23/17.
 */

public class RetrofitAPIClient {

    public static final String BASE_URL = "https://api.zappos.com/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient(){
        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
