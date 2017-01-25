package com.zappos.raakeshpremkumar.ilovezappos.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by raakeshpremkumar on 1/23/17.
 */

public class NetworkUtil {

    private static ConnectivityManager connectivityManager;
    private static NetworkInfo networkInfo;
    private static NetworkInfo.State state;

    public static boolean isNetworkAvailable(Context context){
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null){
            return false;
        }

        state = networkInfo.getState();

        return (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING);

    }

}
