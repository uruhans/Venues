package com.uruhans.code;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity implements IMainView{

    private CoordinatorLayout coordinatorLayout;
    private EditText search;
    private ListView listViewVenues;
    private MainPresenterImpl mPresenter;
    private static final String EXTRA_RX = "EXTRA_RX";
    private static final String SEARCH = "EXTRA_SEARCH";
    private static final String SEARCH_COORDINATES = "EXTRA_SEARCH_COORDINATES";
    private boolean rxCallInWorks = false;
    private String coordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPresenter = new MainPresenterImpl(this);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        listViewVenues = (ListView) findViewById(R.id.venue_list);
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
                rxCallInWorks = true;
                if (search.getText().toString().isEmpty())
                    listViewVenues.setAdapter(null);
                else
                    mPresenter.search(s.toString(), false);
            }
        });

        if(savedInstanceState!=null){
            rxCallInWorks = savedInstanceState.getBoolean(EXTRA_RX);
            coordinates = savedInstanceState.getString(SEARCH_COORDINATES);
            mPresenter.setCoordinates(coordinates);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.locationUnSubscribe();
        mPresenter.rxUnSubscribe();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(EXTRA_RX, rxCallInWorks);
        outState.putString(SEARCH, search.getText().toString());
        outState.putString(SEARCH_COORDINATES, coordinates);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.locationSubscribe();
        mPresenter.search(search.getText().toString(), true);
    }

    @Override
    public void setReply(VenueAdapter venueAdapter) {
        if (venueAdapter != null && !search.getText().toString().isEmpty()) {
            listViewVenues.setAdapter(venueAdapter);
        } else {
            listViewVenues.setAdapter(null);
        }
    }

    @Override
    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public void showMessage(final int messageId, final int message) {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, getString(message), Snackbar.LENGTH_INDEFINITE)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (messageId == R.id.no_network || messageId == R.id.location_disabled) {
                            //startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                        }
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
