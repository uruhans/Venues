package com.rud.uffe.venues;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/** Activity that
 * 1) Finds the actual position via GoogleApiClient
 * 2) uses the Foursquare rest API to get Venues
 * 3) Shows the Venues in a ListView
 *
 * The logic is maintained by the MainPresenter
 */

public class MainActivity extends AppCompatActivity implements MainPresenter.IPresenter,LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    // Goto https://developer.foursquare.com/start, sign up and get CLIENT_ID & CLIENT_SECRET
    final String CLIENT_ID = "xxxx";
    final String CLIENT_SECRET = "zzzz";

    private String searchString;
    private ArrayList<Venue> venuesList;
    private ListView listViewVenues;
    private CoordinatorLayout coordinatorLayout;
    private EditText search;
    private VenueAdapter adapter;
    private AsyncDataFetcher asyncDataFetcher;
    //GoogleApi constants
    private static final long ONE_MIN = 1000 * 60;
    private static final long TWO_MIN = ONE_MIN * 2;
    private static final long FIVE_MIN = ONE_MIN * 5;
    private static final long POLLING_FREQ = 1000 * 30;
    private static final long FASTEST_UPDATE_FREQ = 1000 * 5;
    private static final float MIN_ACCURACY = 25.0f;
    private static final float MIN_LAST_READ_ACCURACY = 500.0f;

    private LocationRequest mLocationRequest;
    private Location mBestReading;
    private GoogleApiClient mGoogleApiClient;
    private MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        presenter = new MainPresenter(this);
        listViewVenues = (ListView) findViewById(R.id.venue_list);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        search = (EditText) findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                presenter.onSearchStringUpdated(s.toString());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void initLocation() {
        MainPresenter.RequestState requestState = MainPresenter.RequestState.OK;
        if (!isNetworkAvailable())
            requestState = MainPresenter.RequestState.NO_NETWORK;
        else if (!locationEnabled())
            requestState = MainPresenter.RequestState.NO_LOCATION_ENABLED;
        else if (!servicesAvailable())
            requestState = MainPresenter.RequestState.NO_SERVICE_INSTALLED;

        presenter.setLocationAvailable(requestState);
        if (requestState == MainPresenter.RequestState.OK) {
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(POLLING_FREQ);
            mLocationRequest.setFastestInterval(FASTEST_UPDATE_FREQ);

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            if (mGoogleApiClient != null) {
                mGoogleApiClient.connect();
            } else {
                Toast.makeText(this, getString(R.string.error_not_connect), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void showSearchResults(ArrayList<Venue> venuesList) {
        //Show the search results
        this.venuesList = venuesList;
        if (venuesList != null  && !search.getText().toString().isEmpty()) {
            adapter = null;
            adapter = new VenueAdapter();
            listViewVenues.setAdapter(adapter);
            Log.d("", "venuesList size : " + venuesList.size());
        } else {
            listViewVenues.setAdapter(null);
        }
    }

    @Override
    public void doSearch(String searchString, String coordinates) {
        //search for the Venues
        if (mGoogleApiClient == null) {
            //This could be null if Location service was off at startup
            initLocation();
        }
        if (asyncDataFetcher != null) {
            asyncDataFetcher.cancel(true);
        }
        if (!TextUtils.isEmpty(searchString) && !TextUtils.isEmpty(coordinates)) {
            //We have to utf-8 encode the query for special characters
            String search = null;
            try {
                search = URLEncoder.encode(searchString, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            asyncDataFetcher = (AsyncDataFetcher) new AsyncDataFetcher(search, coordinates).execute();
        }
    }

    @Override
    public void showError(MainPresenter.RequestState requestState) {
        String error = "";
        switch (requestState) {
            case NO_NETWORK:
                error = getString(R.string.error_network);
                break;
            case NO_SERVICE_INSTALLED:
                error = getString(R.string.error_location_service);
                break;
            case NO_LOCATION_ENABLED:
                error = getString(R.string.error_location);
                break;
            case  RESPONSECODE_NOT_OK:
                error = getString(R.string.error_server_responsecode);
                break;
            case  JSON_NOT_VALID:
                error = getString(R.string.error_json);
                break;
        }

        // Empty response is not a "real" error
        if (requestState != MainPresenter.RequestState.EMPTY_RESPONSE_STR) {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, error, Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            presenter.setLocationAvailable(MainPresenter.RequestState.OK);
                        }
                    });

            // Changing message text color
            snackbar.setActionTextColor(Color.WHITE);

            // Changing snackbar background color
            ViewGroup group = (ViewGroup) snackbar.getView();
            group.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));

            // Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setMaxLines(5);
            textView.setTextColor(Color.RED);

            snackbar.show();
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
    // Get first reading. Get additional location updates if necessary
        if (servicesAvailable() & mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            // Get best last location measurement meeting criteria
            mBestReading = bestLastKnownLocation(MIN_LAST_READ_ACCURACY, FIVE_MIN);
            if (null == mBestReading
                    || mBestReading.getAccuracy() > MIN_LAST_READ_ACCURACY
                    || mBestReading.getTime() < System.currentTimeMillis() - TWO_MIN) {

                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

                // Schedule a runnable to unregister location listeners
                Executors.newScheduledThreadPool(1).schedule(new Runnable() {
                    @Override
                    public void run() {
                        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, MainActivity.this);
                    }
                }, ONE_MIN, TimeUnit.MILLISECONDS);
            }

            if (mBestReading != null) {
                updateCoordinates(String.valueOf(mBestReading.getLatitude()) + "," + String.valueOf(mBestReading.getLongitude()));
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        // Determine whether new location is better than current best estimate
        if (null == mBestReading || location.getAccuracy() < mBestReading.getAccuracy()) {
            mBestReading = location;

            if (mBestReading.getAccuracy() < MIN_ACCURACY) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            }
            updateCoordinates(String.valueOf(location.getLatitude()) + "," +
                    String.valueOf(location.getLongitude()));
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, getString(R.string.error_connect), Toast.LENGTH_LONG).show();
    }

    private Location bestLastKnownLocation(float minAccuracy, long minTime) {
        Location bestResult = null;
        float bestAccuracy = Float.MAX_VALUE;
        long bestTime = Long.MIN_VALUE;

        // Get the best most recent location currently available
        Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mCurrentLocation != null) {
            float accuracy = mCurrentLocation.getAccuracy();
            long time = mCurrentLocation.getTime();

            if (accuracy < bestAccuracy) {
                bestResult = mCurrentLocation;
                bestAccuracy = accuracy;
                bestTime = time;
            }
        }

        // Return best reading or null
        if (bestAccuracy > minAccuracy || bestTime < minTime) {
            return null;
        }
        else {
            return bestResult;
        }
    }

    private void updateCoordinates(String info){
        presenter.setCoordinates(info);
        Log.d("xxxx", "Latitude, Longitude : " + info);
    }

    //get Data from Foursquare async
    private class AsyncDataFetcher extends AsyncTask<View, Void, String> {
        String venuesJson;
        String searchString;
        String coordinates;

        private AsyncDataFetcher(String searchString, String coordinates) {
            super();
            this.searchString = searchString;
            this.coordinates = coordinates;
        }

        @Override
        protected String doInBackground(View... urls) {
            String search = "https://api.foursquare.com/v2/venues/search?client_id=" + CLIENT_ID +
                    "&client_secret=" + CLIENT_SECRET + "&v=20130815&ll=" + coordinates + "&query=" + searchString;
            Log.d("xxxx", search);
            venuesJson = presenter.getVenuesData(search);
            return "";
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(String result) {
            if (TextUtils.isEmpty(venuesJson)) {
                // we have an error to the call
            } else {
                // all things went right
                presenter.handleResponse(venuesJson);
                Log.d("xxxxx", venuesJson);
            }
        }
    }

    private boolean servicesAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0).show();
            return false;
        }
    }

    private boolean locationEnabled() {
        return ((LocationManager) getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    //Adaptor for the ListView
    class VenueAdapter extends ArrayAdapter<Venue> {
        public VenueAdapter() {
            super(MainActivity.this, R.layout.venue_row, venuesList);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            VenueHolder holder = null;

            if (row == null) {
                LayoutInflater inflater = getLayoutInflater();

                row = inflater.inflate(R.layout.venue_row, parent, false);
                holder = new VenueHolder(row);
                row.setTag(holder);
            } else {
                holder = (VenueHolder) row.getTag();
            }
            holder.populateFrom(venuesList.get(position));
            return (row);
        }
    }

    static class VenueHolder {
        private TextView name;
        private TextView address;
        private TextView distance;
        private View row;

        VenueHolder(View row) {
            this.setRow(row);
            name = (TextView) row.findViewById(R.id.name);
            address = (TextView) row.findViewById(R.id.address);
            distance = (TextView) row.findViewById(R.id.distance);
        }

        void populateFrom(Venue r) {
            name.setText(r.getName());
            address.setText(TextUtils.isEmpty(r.getAddress()) ? "na" : r.getAddress());
            distance.setText(r.getDistance() + "");
        }

        public void setRow(View row) {
            this.row = row;
        }

        public View getRow() {
            return row;
        }
    }

}
