package com.tracksecure.common.constants;

public final class EventTypes {
    private EventTypes() {
        throw new UnsupportedOperationException("Event Types is a utility class and cannot be instantiated.");
    }

    public static final String LOCATION_UPDATE = "location-update";
    public static final String TEMPERATURE_ALERT = "temperature-alert";
    public static final String HUMIDITY_ALERT = "humidity-alert";
    public static final String GEOFENCE_ENTRY = "geofence-entry";
    public static final String GEOFENCE_EXIT = "geofence-exit";
    public static final String DEVICE_FAILURE = "device-failure";
    public static final String UNAUTHORIZED_ACCESS = "unauthorized_access";
}
