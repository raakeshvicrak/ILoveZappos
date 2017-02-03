package com.zappos.raakeshpremkumar.ilovezappos.rest;

import android.app.Activity;
import android.view.View;
import android.widget.Toast;

import com.zappos.raakeshpremkumar.ilovezappos.DB.DataBaseManager;
import com.zappos.raakeshpremkumar.ilovezappos.DB.DataBaseQuery;
import com.zappos.raakeshpremkumar.ilovezappos.Utils.NetworkUtil;
import com.zappos.raakeshpremkumar.ilovezappos.model.ProductAPIResponse;
import com.zappos.raakeshpremkumar.ilovezappos.model.Products;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by raakeshpremkumar on 1/31/17.
 */

/*
 * Common Retrofit API class that executes all the API's and makes interface callbacks accordingly by sending the result.
 */
public class ExecuteRestApiSearch {

    private Activity activity;
    private final static String API_KEY = "b743e26728e16b81da139182bb2094357c31d331";
    private ArrayList<Products> products = new ArrayList<Products>();
    private View parentLayout;
    private ApiResultInterface apiResultInterface;
    private long productId;

    public ExecuteRestApiSearch(Activity activity, View parentLayout, long productId){
        this.activity = activity;
        this.parentLayout = parentLayout;
        this.apiResultInterface = (ApiResultInterface) activity;
        this.productId = productId;
    }

    public void executeRestApiSearch(final String searchTerm){

        if (API_KEY.isEmpty()){
            Toast.makeText(activity, "API Key is empty!", Toast.LENGTH_LONG).show();
        }
        else{
            if (NetworkUtil.isNetworkAvailable(activity)){
                RetrofitAPIInterface apiInterface = RetrofitAPIClient.getClient().create(RetrofitAPIInterface.class);

                Call<ProductAPIResponse> call = apiInterface.getTopSearchResults(searchTerm, API_KEY);
                call.enqueue(new Callback<ProductAPIResponse>(){
                    @Override
                    public void onResponse(Call<ProductAPIResponse> call, Response<ProductAPIResponse> response){
                        ArrayList<Products> products = response.body().getResults();
                        setProducts(products);

                        int position = -1;
                        for (int i = 0; i < products.size(); i++){
                            Products products1 = products.get(i);
                            if (products1.getProductId() == productId){
                                position = i;
                                break;
                            }
                        }

                        if (position != -1){
                            products.remove(position);
                        }

                        apiResultInterface.onResult(products, searchTerm);
                    }

                    @Override
                    public void onFailure(Call<ProductAPIResponse> call, Throwable t) {
                        Toast.makeText(activity, ""+t.toString(), Toast.LENGTH_LONG).show();
                    }
                });
            }
            else{
                ArrayList<Products> products_list = DataBaseManager.getInstance(activity).retrieveTablerows(DataBaseQuery.TABLE_PRODUCT_DETAILS,
                        DataBaseQuery.SEARCH_TERM, new String[]{searchTerm});
                setProducts(products_list);
                apiResultInterface.onResult(products, searchTerm);
            }
        }
    }

    public void setProducts(ArrayList<Products> products){
        this.products = products;
    }
}
