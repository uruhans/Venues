package com.rud.uffe.venues;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * The Presenter for the MainActivity
 * Handles user input and the returns state and data to the MainActivity
 */
public class MainPresenter {

    public static enum RequestState {
        OK,
        EMPTY_RESPONSE_STR,
        NO_NETWORK,
        NO_SERVICE_INSTALLED,
        NO_LOCATION_ENABLED,
        RESPONSECODE_NOT_OK,
        JSON_NOT_VALID;
    }
    private IPresenter activity;
    private String coordinates;
    private RequestState requestState = RequestState.OK;

    public MainPresenter(IPresenter activity) {
        this.activity = activity;
    }

    // convert the Foursquare data to ArrayList<Venue>
    public void handleResponse(String jSonStr) {
        if ("".equals(jSonStr)) {
            requestState = RequestState.EMPTY_RESPONSE_STR;
            activity.showError(requestState);
            return;
        }
        ArrayList<Venue> venuesList = parseVenuesJson(jSonStr);
        if (venuesList != null && venuesList.size() > 0) {
            //Sort the Venues by distance
            Collections.sort(venuesList, new Comparator<Object>() {
                public int compare(final Object o1, final Object o2) {
                    final Venue arg1 = (Venue) o1;
                    final Venue arg2 = (Venue) o2;
                    return arg1.compareTo(arg2);
                }
            });
            requestState = RequestState.OK;
            activity.showSearchResults(venuesList);
        } else {
            if (venuesList == null) {
                activity.showError(requestState);
            }
        }
    }

    public ArrayList<Venue> parseVenuesJson(final String response) {
        ArrayList<Venue> venues = new ArrayList<Venue>();
        try {
            // make an jsonObject in order to parse the response
            JSONObject jsonObject = new JSONObject(response);

            // make an jsonObject in order to parse the response
            if (jsonObject.has("response")) {
                if (jsonObject.getJSONObject("response").has("venues")) {
                    JSONArray jsonArray = jsonObject.getJSONObject("response").getJSONArray("venues");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Venue venue = new Venue();
                        if (jsonArray.getJSONObject(i).has("name")) {
                            venue.setName(jsonArray.getJSONObject(i).getString("name"));

                            if (jsonArray.getJSONObject(i).has("location")) {
                                if (jsonArray.getJSONObject(i).getJSONObject("location").has("address")) {
                                    venue.setAddress(jsonArray.getJSONObject(i).getJSONObject("location").getString("address"));
                                }
                                if (jsonArray.getJSONObject(i).getJSONObject("location").has("distance")) {
                                    venue.setDistance(jsonArray.getJSONObject(i).getJSONObject("location").getInt("distance"));
                                }
                                venues.add(venue);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            //return null because the JSon is not valid
            requestState = RequestState.JSON_NOT_VALID;
            return null;
        }
        return venues;
    }

    public void onSearchStringUpdated(String searchString) {
        if ("".equals(searchString)) {
            activity.showSearchResults(new ArrayList<Venue>());
        } else {
            if (requestState == RequestState.OK) {
                Log.d("xxxx", "do search");
                activity.doSearch(searchString, coordinates);
            } else {
                activity.showError(requestState);
            }
        }
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public void setLocationAvailable(RequestState requestState) {
        this.requestState = requestState;
        if (!(requestState == RequestState.OK)) {
            activity.showError(requestState);
        }
    }

    public String getVenuesData(String theUrl) {
        // string buffers the url
        StringBuilder result = new StringBuilder();
        HttpURLConnection conn = null;
        try {
            URL url = new URL(theUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestMethod("GET");
            conn.setDoOutput(false);

            InputStream in = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                requestState = RequestState.RESPONSECODE_NOT_OK;
                activity.showError(requestState);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return result.toString().trim();
    }

    public interface IPresenter {
        void showSearchResults(ArrayList<Venue> venuesList);
        void doSearch(String searchString, String coordinates);
        void showError(RequestState requestState);
    }
}
