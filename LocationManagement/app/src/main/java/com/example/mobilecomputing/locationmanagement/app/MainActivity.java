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
        Intent locationServiceIntent = new Intent(this, LocationService.class);
        this.startService(locationServiceIntent);
        bindService(locationServiceIntent, this, BIND_AUTO_CREATE);
    }

    public void stopService(View view) {
        Intent locationServiceIntent = new Intent(getApplicationContext(), LocationService.class);
        stopService(locationServiceIntent);
        unbindService(this);
    }

    public void updateFields(View view) {
        TextView testText = (TextView) findViewById(R.id.testText);
        try {
            testText.setText(locationService.getLatitude() + "---" + locationService.getLongitude());
        } catch (RemoteException e) {
            System.out.println("Remote exception...");
        }
    }
}
