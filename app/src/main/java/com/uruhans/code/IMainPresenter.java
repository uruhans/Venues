package com.uruhans.code;

/**
 * Created by uffhan on 17-02-2016.
 */
public interface IMainPresenter {
    void search(String searchString, boolean useCache);
    void setCoordinates(String coordinates);
    void rxUnSubscribe();
    void eventBusSubscribe();
    void eventBusUnSubscribe();
    void networkAvailable(boolean available);
}
