package com.example.mobilecomputing.locationmanagement.app;

/**
 * Created by SebastianHesse on 23.06.2015.
 */
public class TrackPoint {

    private double latitude;
    private double longitude;
    private double speed;
    private double time;
    private double distance;

    public TrackPoint(final double latitude, final double longitude, final double time) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(final double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(final double longitude) {
        this.longitude = longitude;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(final double speed) {
        this.speed = speed;
    }

    public double getTime() {
        return time;
    }

    public void setTime(final double time) {
        this.time = time;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(final double distance) {
        this.distance = distance;
    }
}
