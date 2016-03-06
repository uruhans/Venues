package com.uruhans.code;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import rx.Observable;
import rx.Observer;
import rx.Subscription;

/**
 * Created by uffhan on 17-02-2016.
 */
public class FoursquareInteractorImpl implements IFoursquareInteractor {
    // Goto https://developer.foursquare.com/start, sign up and get CLIENT_ID & CLIENT_SECRET
    //final String CLIENT_ID = "xxxx";
    //final String CLIENT_SECRET = "zzzz";

    final String CLIENT_ID = "NRFHU1NDVU0QXWOD5INDVODK0G0NITXIGV3X4IHIEAZUZSYI";
    final String CLIENT_SECRET = "COUI5PMRCTAFPZVKQ01JQOVPR4QFTGIUGF4ZCCEWO12TAQKD";

    final String v = "20130815";

    private Context mContext;
    private FoursquareInteractorListener mListener;

    private FoursquareService service;
    private Subscription subscription;


    FoursquareInteractorImpl(Context context, FoursquareInteractorListener listener, FoursquareService service) {
        this.mContext = context;
        this.mListener = listener;
        this.service = service;
    }

    @Override
    public void getServerData(String latLong, String search, boolean useCache) {
        if (!useCache)
            service.clearCache();
        Log.d("cccc", search);
        Observable<Venues> venueResponseObservable = (Observable<Venues>)
                service.getPreparedObservable(service.getAPI().getVenuesObservable(CLIENT_ID,
                        CLIENT_SECRET,
                        v,
                        latLong,
                        search), Venues.class, true, useCache);
        subscription = venueResponseObservable.subscribe(new Observer<Venues>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.d("xxx", "" + e.getMessage());
                mListener.onDataError(e.getMessage());
            }

            @Override
            public void onNext(Venues response) {
                Log.d("xxx", "" + response.response.venues.length);
                mListener.onData(response);
            }
        });
    }

    @Override
    public void checkNetwork() {
        boolean netw = isNetworkAvailable();
        mListener.onNetworkchecked(netw);
    }

    @Override
    public void unSubscribe() {
        if(subscription!=null && !subscription.isUnsubscribed())
            subscription.unsubscribe();
    }

    // Check if the device has network access
    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public interface FoursquareInteractorListener {
        void onData(Venues venues);
        void onNetworkchecked(boolean networkok);
        void onDataError(String exception);
    }
}
