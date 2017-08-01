package com.shareexpenses.app;

import android.app.Application;

/**
 * Created by jess on 10/09/2014.
 */
public class MainApplication extends Application {

    private Data data;
    private static MainApplication mainApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mainApplication=this;
        data = new Data(this);
    }

    public Data getData() {
        return data;
    }

    public static MainApplication getInstance() {
        return mainApplication;
    }
}
