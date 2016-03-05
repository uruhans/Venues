package com.uruhans.code;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by uffhan on 17-02-2016.
 */
public class MainPresenterImpl implements IMainPresenter, MainInteractorImpl.MainInteractorListener, VenueAdapter.VenueAdapterListener {

    //Copenhagen 2100
    private String coordinates = "57.702522,12.5901542";
    private IMainView mView;
    private IMainInteractor mInteractor;
    private VenueAdapter adapter;

    public MainPresenterImpl(IMainView mView, NetworkService service) {
        this.mView = mView;
        this.mInteractor = new MainInteractorImpl((Activity) mView, this, service);
    }

    @Override
    public void onData(Venues venues) {
        ArrayList<Venues.Response.Venue> mVenues = new ArrayList<Venues.Response.Venue>();
        for (Venues.Response.Venue venue:venues.response.venues) {
            mVenues.add(venue);
        }

        adapter = new VenueAdapter((Activity) mView, mVenues, this);
        mView.setReply(adapter);
    }

    //Message from EventBus
    public void onEvent(LocationEvent event){
        if (event.getId() == R.id.new_location) {
            Log.d("XXXX", "we have location : " + event.getMessage());
            coordinates = event.getMessage();
            mView.setCoordinates(coordinates);
        } else if (event.getId() == R.id.location_disabled) {
            Log.d("XXXX", "location disabled");
            mView.showMessage(R.id.location_disabled, R.string.error_location);
            mView.restartLocationService();
        } else if (event.getId() == R.id.no_google_api) {
            Log.d("XXXX", "missing google api");
            mView.showMessage(R.id.no_google_api, R.string.error_location_service);
        }
    }

    @Override
    public void onDataError(String exception) {
        Log.d("XXXX", "onData : " + exception );
        mView.showMessage(R.id.server_error, R.string.error_server_responsecode);
    }

    @Override
    public void search(String searchString, boolean useCache) {
        mInteractor.getServerData(coordinates, searchString, useCache);
    }

    @Override
    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public void rxUnSubscribe(){
        mInteractor.unSubscribe();
    }

    @Override
    public void eventBusSubscribe() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void eventBusUnSubscribe() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void networkAvailable(boolean available) {
        if (!available)
            mView.showMessage(R.id.no_network, R.string.error_network);
    }

    @Override
    public void onMapClicked(Venues.Response.Venue venue) {
        mView.launchMap(venue.location.lat + "," + venue.location.lng, venue.name);
    }

    @Override
    public void onGoogleClicked(Venues.Response.Venue venue) {
        mView.launchGoogle(venue.name, venue.location.address);
    }

    @Override
    public void onPhoneClicked(Venues.Response.Venue venue) {
       mView.launchDialer(venue.contact.phone);
    }
}
