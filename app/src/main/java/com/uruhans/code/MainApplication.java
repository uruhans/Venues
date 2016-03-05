package com.uruhans.code;

import android.app.Application;

/**
 * Created by uruha on 28-02-2016.
 */
public class MainApplication extends Application {
    private NetworkService networkService;

    @Override
    public void onCreate() {
        super.onCreate();

        networkService = new NetworkService();

    }

    public NetworkService getNetworkService() {
        return networkService;
    }
}
