package com.example.mobilecomputing.locationmanagement.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.example.mobilecomputing.locationmanagement.app.util.SystemUiHider;


/**
 * An example full-screen activity that shows and hides the system UI (i.e. status bar and navigation/system bar) with
 * user interaction.
 *
 * @see SystemUiHider
 */
public class MainActivity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

    }

    public void startService(View view) {
        Intent locationServiceIntent = new Intent(getApplicationContext(), LocationService.class);
        getApplicationContext().startService(locationServiceIntent);
    }

    public void stopService(View view) {
        Intent locationServiceIntent = new Intent(getApplicationContext(), LocationService.class);
        getApplicationContext().stopService(locationServiceIntent);

    }
}
