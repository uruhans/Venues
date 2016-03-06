package com.uruhans.code;

/**
 * Interface for the MainActivity
 */
public interface IMainView {

    void setReply(VenueAdapter venueAdapter);
    void setCoordinates(String coordinates);
    void showMessage(int messageId, int message);
}
