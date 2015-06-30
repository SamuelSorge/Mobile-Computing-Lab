package com.example.mobilecomputing.locationmanagement.app;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
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
    private double speedSum = 0;

    final LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(final Location location) {
            Toast.makeText(getApplicationContext(), "Location changed...", Toast.LENGTH_SHORT).show();
            TrackPoint tp = new TrackPoint(location.getLatitude(), location.getLongitude(), location.getElapsedRealtimeNanos() / 1000000000);
            calcDistanceAndSpeed(tp);
        }

        private void calcDistanceAndSpeed(final TrackPoint tp) {
            double speed = 0;
            if (trackPointList.size() == 0) {
                tp.setDistance(0);
            } else {
                TrackPoint lastPoint = trackPointList.get(trackPointList.size() - 1);
                final double distance = getDistance(lastPoint, tp);
                final double period = tp.getTime() - lastPoint.getTime();

                if (period != 0) {
                    speed = distance / period;
                }

                tp.setDistance(lastPoint.getDistance() + distance);
            }
            trackPointList.add(tp);
            speedSum += speed;
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
        return new LocationServiceImpl();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        this.trackPointList.clear();
        this.speedSum = 0;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        this.trackPointList.clear();
        this.speedSum = 0;
        Toast.makeText(this, "Location Service is starting...", Toast.LENGTH_SHORT).show();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 1, locationListener, getMainLooper());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        log("unbind called...");
        locationManager.removeUpdates(locationListener);
        log("state: " + Environment.getExternalStorageState());
        File sdDir = Environment.getExternalStorageDirectory();
        log("sd dir:" + sdDir.getPath());
        File trackFile = new File(sdDir, LOG_FILE);
        log("Trying to write tracks to file...");
        if (!trackFile.exists() || trackFile.exists() && trackFile.delete()) {
            log("Old track file deleted.");
            log("Start writing to new track file...");
            writeTrackPointsToLogFile(trackFile);
        }
        Toast.makeText(this, "Location Service has stopped...", Toast.LENGTH_SHORT).show();
        return super.onUnbind(intent);
    }

    private void writeTrackPointsToLogFile(final File trackFile) {
        log("1");
        try {
            log("2");
            if (trackFile.createNewFile()) {
                log("2 if");
                FileOutputStream fileOutputStream = new FileOutputStream(trackFile);
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
                log("DATA: " + sb.toString());
                fileOutputStream.write(sb.toString().getBytes());
                fileOutputStream.flush();
                fileOutputStream.close();
                log("Flushed data to file...");
            } else {
                log("2 else");
            }
        } catch (IOException e) {
            log("Error during file access...");
            e.printStackTrace();
        }
        log("3");
    }

    private float getDistance(final TrackPoint lastPoint, final TrackPoint tp) {
        float[] result = new float[3];
        Location.distanceBetween(lastPoint.getLatitude(), lastPoint.getLongitude(), tp.getLatitude(), tp.getLongitude(), result);
        return result[0];
    }

    private void addTrackPointToString(final StringBuilder sb, final TrackPoint tp) {
        sb.append("        <trkpt lat=\"" + tp.getLatitude() + "\" lon=\"" + tp.getLongitude() + "\">")
                .append("<cmt>" + tp.getDistance() + "</cmt>")
                .append("<time>" + tp.getTime() + "</time>")
                .append("</trkpt>\n");
    }

    private static void log(String logMessage) {
        Log.i("LocationService", logMessage);
    }

    private class LocationServiceImpl extends ILocationService.Stub {
        public double getLatitude() {
            if (!trackPointList.isEmpty()) {
                return trackPointList.get(trackPointList.size() - 1).getLatitude();
            } else {
                return 0;
            }
        }

        public double getLongitude() {
            if (!trackPointList.isEmpty()) {
                return trackPointList.get(trackPointList.size() - 1).getLongitude();
            } else {
                return 0;
            }
        }

        public double getDistance() {
            if (!trackPointList.isEmpty()) {
                return trackPointList.get(trackPointList.size() - 1).getDistance();
            } else {
                return 0;
            }
        }

        public double getAverageSpeed() {
            if (!trackPointList.isEmpty()) {
                return speedSum / trackPointList.size();
            } else {
                return 0;
            }
        }
    } // End of LocationService Stub implementation
}
