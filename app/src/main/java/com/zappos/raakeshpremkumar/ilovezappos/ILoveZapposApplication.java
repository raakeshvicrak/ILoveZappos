package com.zappos.raakeshpremkumar.ilovezappos;

import android.app.Application;

import com.zappos.raakeshpremkumar.ilovezappos.DB.DataBaseManager;

/**
 * Created by raakeshpremkumar on 1/29/17.
 */

public class ILoveZapposApplication extends Application {
    @Override
    public void onCreate(){
        super.onCreate();
        DataBaseManager.setContext(this);
    }
}
