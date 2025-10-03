package com.tracksecure.common.util;

public final class GeoLocationUtil {

    public GeoLocationUtil(){
        throw new UnsupportedOperationException("GeoLocationUtil is a utility class and cannot be instantiated.");
    }

    private static final double EARTH_RADIUS_KM = 6378137.0;

    // Here we try calculate the distance between two locations using Haversine formula
    public double calculateDistance(double lon1,double lat1,double lon2,double lat2){
        double dLat = Math.toRadians(lat2-lat1); // Distance between latitudes in radian
        double dLon = Math.toRadians(lon2-lon1); // Distance between longitudes in radian

        double a = Math.sin(dLat/2) * Math.sin(dLat/2)
                + Math.cos(Math.toRadians(lat1) * Math.cos(Math.toRadians(lat2)))
                * Math.sin(dLon/2) * Math.sin(dLon/2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return EARTH_RADIUS_KM * c;
    }
}
