package com.example.mobilecomputing.locationmanagement.app;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SebastianHesse on 16.06.2015.
 */
public class LocationService extends Service {

    private static final String LOG_FILE = "tracks.gpx";

    private LocationManager locationManager;
    private List<TrackPoint> trackPointList = new ArrayList<>();

    private class LocationServiceImpl extends LocationService.Stub
    {
        public double getLatitude()
        {
            return trackPointList.get(trackPointList.size()-1).getLatitude();
        }
        public double getLongitude()
        {
            return trackPointList.get(trackPointList.size()-1).getLongitude();
        }
        public double getDistance() {
            return trackPointList.get(trackPointList.size() - 1).getDistance();
        }

        public double getAverageSpeed()
        {
            return trackPointList.get(trackPointList.size()-1).getSpeed();
        }
    } // End of LocationService Stub implementation

    final LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(final Location location) {
            Toast.makeText(getApplicationContext(), "Location changed...", Toast.LENGTH_SHORT).show();
            System.out.println("location latitude:"+location.getLatitude());
            System.out.println("location longitude:"+location.getLongitude());
            TrackPoint tp = new TrackPoint(location.getLatitude(), location.getLongitude(), location.getSpeed(), location.getTime());
            if (trackPointList.size() == 0) {
                tp.setDistance(0);
            } else {
                TrackPoint lastPoint = trackPointList.get(trackPointList.size() - 1);
                tp.setDistance(getDistance(lastPoint, tp));
            }
            trackPointList.add(tp);
        }

        @Override
        public void onStatusChanged(final String provider, final int status, final Bundle extras) {

        }

        @Override
        public void onProviderEnabled(final String provider) {

        }

        @Override
        public void onProviderDisabled(final String provider) {

        }
    };

    public LocationService() {
        super();
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        Toast.makeText(this, "Location Service is starting...", Toast.LENGTH_SHORT).show();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 1, locationListener, getMainLooper());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        locationManager.removeUpdates(locationListener);
        File sdDir = Environment.getExternalStorageDirectory();
        File trackFile = new File(sdDir, LOG_FILE);
        if (trackFile.delete()) {
            System.out.println("Deleted old track file and start writing to new one...");
            writeTrackPointsToLogFile(trackFile);
        }
        Toast.makeText(this, "Location Service has stopped...", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    private void writeTrackPointsToLogFile(final File trackFile) {
        try (FileWriter fileWriter = new FileWriter(trackFile);) {
            StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n")
                    .append("<gpx version=\"1.1\" creator=\"MobileComputing\">\n")
                    .append("  <metadata> <!-- Metadaten --> </metadata>");
            sb.append("    <trk>\n");
            sb.append("      <trkseg>\n");
            for (int i = 0; i < trackPointList.size(); i++) {
                TrackPoint tp = trackPointList.get(i);
                addTrackPointToString(sb, tp);
            }
            sb.append("      </trkseg>\n");
            sb.append("    </trk>\n");
            sb.append("</gpx>");

            fileWriter.write(sb.toString());
        } catch (IOException e) {
            System.out.println("Error during file access...");
        }
    }

    private float getDistance(final TrackPoint lastPoint, final TrackPoint tp) {
        float[] result = new float[3];
        Location.distanceBetween(lastPoint.getLatitude(), lastPoint.getLongitude(), tp.getLatitude(), tp.getLongitude(), result);
        return result[0];
    }

    private void addTrackPointToString(final StringBuilder sb, final TrackPoint tp) {
        sb.append("        <trkpt lat=\""+tp.getLatitude()+"\" lon=\""+tp.getLongitude()+"\">")
                .append("<cmt>"+tp.getDistance()+"</cmt>")
                .append("<time>" + tp.getTime() + "</time>")
                .append("</trkpt>\n");
    }
}
