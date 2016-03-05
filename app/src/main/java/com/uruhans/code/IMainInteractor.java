package com.uruhans.code;

/**
 * Created by uffhan on 17-02-2016.
 */
public interface IMainInteractor {
    void getServerData(String latLong, String search, boolean useCache);
    void unSubscribe();

}
