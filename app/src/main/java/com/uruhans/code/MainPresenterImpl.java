package com.uruhans.code;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * This presenter handles communication between the MainActivity and the Location service, the data provider and the model
 */
public class MainPresenterImpl implements IMainPresenter, FoursquareInteractorImpl.FoursquareInteractorListener, VenueAdapter.VenueAdapterListener {

    //Göteborg, Sweeden
    private String coordinates = "57.717437,11.962470";
    private IMainView mView;
    private IFoursquareInteractor foursquareInteractor;
    private ILocationInteractor locationInteraction;
    private VenueAdapter adapter;
    private String searchString;
    private FoursquareService service;
    private Context context;
    private boolean mUseCache;

    public MainPresenterImpl(IMainView mView) {
        this.mView = mView;
        this.context = (Activity) mView;
        MainApplication mainApplication = (MainApplication) context.getApplicationContext();
        service = mainApplication.getNetworkService();
        this.foursquareInteractor = new FoursquareInteractorImpl((Activity) mView, this, service);
        locationInteraction = new LocationInteractionImpl((Activity) mView);
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

    @Override
    public void onNetworkchecked(boolean networkok) {
        if (networkok) {
            foursquareInteractor.getServerData(coordinates, searchString, mUseCache);
        } else {
            mView.showMessage(R.id.no_network, R.string.error_network);
        }
    }

    public void onEvent(LocationEvent event){
        switch (event.getId()) {
            case R.id.new_location : {
                Log.d("XXXX", "we have location : " + event.getMessage());
                coordinates = event.getMessage();
                mView.setCoordinates(coordinates);
                if (!TextUtils.isEmpty(searchString))
                    search(searchString, false);
                break;
            }
            case R.id.location_disabled : {
                Log.d("XXXX", "location disabled");
                mView.showMessage(R.id.location_disabled, R.string.error_location);
                //mView.restartLocationService();
                break;
            }
            case R.id.no_google_api : {
                Log.d("XXXX", "missing google api");
                mView.showMessage(R.id.no_google_api, R.string.error_location_service);
                break;
            }
            default: {
                Log.d("XXXX", "unknown Event from EventBus ???");
            }
        }
    }

    @Override
    public void onDataError(String exception) {
        Log.d("XXXX", "onData : " + exception );
        mView.showMessage(R.id.server_error, R.string.error_server_responsecode);
    }

    @Override
    public void search(String searchString, boolean useCache) {
        mUseCache = useCache;
        this.searchString = searchString;
        foursquareInteractor.checkNetwork();
    }

    @Override
    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public void rxUnSubscribe(){
        foursquareInteractor.unSubscribe();
    }

    @Override
    public void locationSubscribe() {
        EventBus.getDefault().register(this);
        locationInteraction.startService();;
    }

    @Override
    public void locationUnSubscribe() {
        locationInteraction.stopService();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void networkAvailable(boolean available) {
        if (!available)
            mView.showMessage(R.id.no_network, R.string.error_network);
    }

    @Override
    public void onMapClicked(Venues.Response.Venue venue) {
        ILauncher launcher = new Launcher();
        launcher.launchGoogleMaps(context, venue.location.lat + "," + venue.location.lng, venue.name);
    }

    @Override
    public void onGoogleClicked(Venues.Response.Venue venue) {
        ILauncher launcher = new Launcher();
        launcher.launchGoogle(context, venue.name + venue.location.address);
    }

    @Override
    public void onPhoneClicked(Venues.Response.Venue venue) {
        ILauncher launcher = new Launcher();
        launcher.launchDial(context, venue.contact.phone);
    }
}
