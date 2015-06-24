package com.example.mobilecomputing.locationmanagement.app;

/**
 * Created by SebastianHesse on 23.06.2015.
 */
interface ILocationService {

    double getLatitude();

    double getLongitude();

    double getDistance();

    double getAverageSpeed();
}
