package com.example.mobilecomputing.locationmanagement.app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends Activity implements ServiceConnection {

    private ILocationService locationService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    @Override
    public void onServiceConnected(final ComponentName name, final IBinder service) {
        System.out.println("Received connected service object...");
        locationService = ILocationService.Stub.asInterface(service);
    }

    @Override
    public void onServiceDisconnected(final ComponentName name) {
        System.out.println("Disconnect service...");
        locationService = null;
    }

    public void startService(View view) {
        try {
            Intent locationServiceIntent = new Intent(this, LocationService.class);
            this.startService(locationServiceIntent);
            bindService(locationServiceIntent, this, BIND_AUTO_CREATE);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void stopService(View view) {
        try {
            Intent locationServiceIntent = new Intent(getApplicationContext(), LocationService.class);
            stopService(locationServiceIntent);
            unbindService(this);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateFields(View view) {
        TextView latitudeValue = (TextView) findViewById(R.id.latitudeValue);
        TextView longitudeValue = (TextView) findViewById(R.id.longitudeValue);
        TextView distance = (TextView) findViewById(R.id.distanceValue);
        TextView speed = (TextView) findViewById(R.id.speedValue);
        try {
            latitudeValue.setText("" + locationService.getLatitude());
            longitudeValue.setText("" + locationService.getLongitude());
            distance.setText("" +  locationService.getDistance());
            speed.setText("" + locationService.getAverageSpeed());
        } catch (RemoteException e) {
            System.out.println("Remote exception...");
        }
    }
}
