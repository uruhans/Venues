package com.uruhans.code;

/**
 * Interface for MainPresenter
 */
public interface IMainPresenter {
    void search(String searchString, boolean useCache);
    void setCoordinates(String coordinates);
    void rxUnSubscribe();
    void locationSubscribe();
    void locationUnSubscribe();
    void networkAvailable(boolean available);
}
