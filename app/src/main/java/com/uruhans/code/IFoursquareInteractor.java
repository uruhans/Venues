package com.uruhans.code;

/**
 * Interface for FoursquareInteractorImpl
 */
public interface IFoursquareInteractor {
    void getServerData(String latLong, String search, boolean useCache);
    void checkNetwork();
    void unSubscribe();

}
