package com.example.mobilecomputing.locationmanagement.app;

/**
 * Created by SebastianHesse on 23.06.2015.
 */
public class TrackPoint {

    private double latitude;
    private double longitude;
    private float speed;
    private long time;

    public TrackPoint(final double latitude, final double longitude, final float speed, final long time) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
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

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(final float speed) {
        this.speed = speed;
    }

    public long getTime() {
        return time;
    }

    public void setTime(final long time) {
        this.time = time;
    }
}
