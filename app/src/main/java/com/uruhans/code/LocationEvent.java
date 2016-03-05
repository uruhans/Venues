package com.uruhans.code;

/**
 * Created by uruha on 29-02-2016.
 */
public class LocationEvent {
    private final int id;
    private final String message;

    public LocationEvent(int id, String message) {
        this.id = id;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }
}
