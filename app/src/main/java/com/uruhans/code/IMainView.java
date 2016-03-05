package com.uruhans.code;

/**
 * Created by uffhan on 17-02-2016.
 */
public interface IMainView {

    void setReply(VenueAdapter venueAdapter);
    void launchMap(String coordinates, String name);
    void launchGoogle(String name, String address);
    void launchDialer(String phoneNumber);
    void setCoordinates(String coordinates);
    void showMessage(int messageId, int message);
    void restartLocationService();

}
