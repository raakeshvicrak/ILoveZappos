package com.zappos.raakeshpremkumar.ilovezappos.model;

import java.util.ArrayList;
import com.google.gson.annotations.SerializedName;

/**
 * Created by raakeshpremkumar on 1/23/17.
 */

public class ProductAPIResponse {

    @SerializedName("results")
    private ArrayList<Products> results;

    @SerializedName("currentResultCount")
    private int currentResultCount;

    @SerializedName("totalResultCount")
    private int totalResultCount;


    public ArrayList<Products> getResults() {
        return results;
    }

    public void setResults(ArrayList<Products> results) {
        this.results = results;
    }

    public int getCurrentResultCount() {
        return currentResultCount;
    }

    public void setCurrentResultCount(int currentResultCount) {
        this.currentResultCount = currentResultCount;
    }

    public int getTotalResultCount() {
        return totalResultCount;
    }

    public void setTotalResultCount(int totalResultCount) {
        this.totalResultCount = totalResultCount;
    }
}
