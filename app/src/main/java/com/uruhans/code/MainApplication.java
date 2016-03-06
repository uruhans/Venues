package com.uruhans.code;

import android.app.Application;

/**
 * The global context for the app
 */
public class MainApplication extends Application {
    private FoursquareService networkService;

    @Override
    public void onCreate() {
        super.onCreate();
        networkService = new FoursquareService();
    }

    public FoursquareService getNetworkService() {
        return networkService;
    }
}
