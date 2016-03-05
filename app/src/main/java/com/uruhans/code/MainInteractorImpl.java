package com.uruhans.code;

import android.content.Context;
import android.util.Log;

import rx.Observable;
import rx.Observer;
import rx.Subscription;

/**
 * Created by uffhan on 17-02-2016.
 */
public class MainInteractorImpl implements IMainInteractor {
    // Goto https://developer.foursquare.com/start, sign up and get CLIENT_ID & CLIENT_SECRET
    final String CLIENT_ID = "xxxx";
    final String CLIENT_SECRET = "zzzz";

    final String v = "20130815";

    private Context mContext;
    private MainInteractorListener mListener;

    private NetworkService service;
    private Subscription subscription;


    MainInteractorImpl(Context context, MainInteractorListener listener, NetworkService service) {
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
    public void unSubscribe() {
        if(subscription!=null && !subscription.isUnsubscribed())
            subscription.unsubscribe();
    }

    public interface MainInteractorListener {
        void onData(Venues venues);
        void onDataError(String exception);
    }
}
