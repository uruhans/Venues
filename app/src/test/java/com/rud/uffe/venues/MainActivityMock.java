package com.rud.uffe.venues;

import java.util.ArrayList;

/**
 * This is a Mock for the MainActivity
 */
public class MainActivityMock implements MainPresenter.IPresenter {
    private MainPresenter presenter;
    private ArrayList<Venue> venuesList;
    private MainPresenter.RequestState requestState = MainPresenter.RequestState.OK;
    private String searchStr;
    private boolean beenThere;

    public void setPresenter() {
        presenter = new MainPresenter(this);
    }

    @Override
    public void showSearchResults(ArrayList<Venue> venuesList) {
        beenThere = true;
    }

    @Override
    public void doSearch(String searchString, String coordinates) {
        searchStr = searchString;
        beenThere = true;
    }

    @Override
    public void showError(MainPresenter.RequestState requestState) {
        this.requestState = requestState;
        beenThere = true;
    }

    public ArrayList<Venue> getVenueList() {
        return venuesList;
    }

    public String getSearchString() {
        return searchStr;
    }

    public MainPresenter.RequestState getRequestState() {
        return requestState;
    }

    public void handleJson(String venuesJson) {
        setPresenter();
        presenter.handleResponse("");
    }

    public void checkDoSearch(String search, String coord) {
        setPresenter();
        presenter.setCoordinates(coord);
        presenter.onSearchStringUpdated(search);
    }

    public void showSearchResults() {
        setPresenter();
        presenter.onSearchStringUpdated("");
    }

    public boolean getBeenThere() {
        return beenThere;
    }

}
