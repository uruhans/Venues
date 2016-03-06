package com.uruhans.code;

/**
 * LocationEvent is used for sending id/message by EventBus
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
