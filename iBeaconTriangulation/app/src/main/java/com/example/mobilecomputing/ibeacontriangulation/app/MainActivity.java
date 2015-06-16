package com.example.mobilecomputing.ibeacontriangulation.app;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.*;

public class MainActivity extends Activity {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 10000;

    private ArrayAdapter<String> mDevicesArrayAdapter;
    private Map<BluetoothDevice, List<Advertisement>> beacons = new HashMap<>(3);
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler = new Handler();
    private boolean mScanning;
    private List<CoordPoint> points = new ArrayList<>(3);

    {
        points.add(new CoordPoint("00:1A:7D:DA:71:13", 0, 0));
        points.add(new CoordPoint("00:1A:7D:DA:71:07", 1, 0));
        points.add(new CoordPoint("5C:F3:70:61:43:C8", 0, 6));
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi,
                                     final byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Advertisement advertisement = new Advertisement(scanRecord, rssi);
                                List<Advertisement> advertisements;
                                if (!beacons.containsKey(device)) {
                                    advertisements = new ArrayList<>();
                                    beacons.put(device, advertisements);
                                } else {
                                    advertisements = beacons.get(device);
                                }
                                advertisements.add(advertisement);
                            } catch (Exception e) {
                                System.out.println("Error.");
                            }
                        }
                    });
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        mDevicesArrayAdapter = new ArrayAdapter<>(this, R.layout.device_list, R.id.deviceName);

        ListView newDevicesListView = (ListView) findViewById(R.id.deviceList);
        newDevicesListView.setAdapter(mDevicesArrayAdapter);
    }

    public void scanDevices(View view) {
        mDevicesArrayAdapter.clear();

        // Stops scanning after a pre-defined scan period.
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScanning = false;
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                generateDeviceList();
            }
        }, SCAN_PERIOD);

        mScanning = true;
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }

    /**
     * Fills the device list with information to device, distance and numbers of received advertisements.
     */
    private void generateDeviceList() {
        Map<String, Double> distances = new HashMap<>();
        for (BluetoothDevice device : beacons.keySet()) {
            List<Advertisement> advertisements = beacons.get(device);
            String prefix = "";
            String uuid = "";
            String major = "";
            String minor = "";
            int rssiSum = 0;
            int txPowerSum = 0;
            for (Advertisement ad : advertisements) {
                rssiSum += ad.rssi;
                txPowerSum += ad.txPower;
                prefix = ad.prefix;
                uuid = ad.uuid;
                major = ad.major;
                minor = ad.minor;
            }

            int calculatedRssi = rssiSum / advertisements.size();
            int calculatedTxPower = txPowerSum / advertisements.size();
            double calculatedDistance = getDistance(calculatedRssi, calculatedTxPower);
            distances.put(device.getAddress(), calculatedDistance);

            StringBuilder deviceDetails = new StringBuilder();
            deviceDetails.append("device: ").append(device.getName());
            deviceDetails.append(", device address: ").append(device.getAddress());
            deviceDetails.append(", prefix: ").append(prefix)
                    .append(", uuid: ").append(uuid)
                    .append(", major: ").append(major)
                    .append(", minor: ").append(minor);
            deviceDetails.append(", txPower: ").append(calculatedTxPower);
            deviceDetails.append(", rssi: ").append(calculatedRssi);
            deviceDetails.append(", distance: ").append(calculatedDistance);
            deviceDetails.append(", advertisements: ").append(advertisements.size());

            mDevicesArrayAdapter.add(deviceDetails.toString());
        }

        final CoordPoint beacon1 = points.get(0);
        final CoordPoint beacon2 = points.get(1);
        final CoordPoint beacon3 = points.get(2);
        CoordPoint position = getLocationByTrilateration(beacon1, distances.get(beacon1.getDeviceAddress()),
                beacon2, distances.get(beacon2.getDeviceAddress()), beacon3, distances.get(beacon3.getDeviceAddress()));
        TextView devicePosition = (TextView) findViewById(R.id.devicePosition);
        devicePosition.setText("X: "+position.getX()+", Y: "+position.getY());
    }

    private String convertByteToHex(final byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }

    private String convertByteToString(final byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append((int) b);
        }
        return sb.toString();
    }

    private int convertByteToInteger(final byte[] bytes) {
        return (int) bytes[0];
    }

    /**
     * http://electronics.stackexchange.com/questions/83354/calculate-distance-from-rssi
     *
     * RSSI = TxPower - 10 * n * lg(d) n = 2 (in free space)
     * <p/>
     * d = 10 ^ ((TxPower - RSSI) / (10 * n))
     *
     * @param rssi    current rssi
     * @param txPower reference rssi at 1m
     * @return distance
     */
    private double getDistance(int rssi, int txPower) {
        return Math.pow(10d, ((double) txPower - rssi) / (10 * 2));
    }

    /**
     * alternative method to get distance.
     * http://stackoverflow.com/questions/20416218/understanding-ibeacon-distancing
     *
     * @param txPower
     * @param rssi
     * @return
     */
    private double calculateAccuracy(double rssi, int txPower) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }

        double ratio = rssi*1.0/txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio,10);
        }
        else {
            return (0.89976)*Math.pow(ratio,7.7095) + 0.111;
        }
    }

    /**
     * Clears device list and beacon map so that a user can make a "fresh" scan.
     * @param view
     */
    public void clearList(View view) {
        this.mDevicesArrayAdapter.clear();
        this.beacons.clear();
    }

    public CoordPoint getLocationByTrilateration(
            CoordPoint point1, double distance1,
            CoordPoint point2, double distance2,
            CoordPoint point3, double distance3){

        //DECLARACAO DE VARIAVEIS
        CoordPoint result = new CoordPoint("ANDROID");
        double[] P1   = new double[2];
        double[] P2   = new double[2];
        double[] P3   = new double[2];
        double[] ex   = new double[2];
        double[] ey   = new double[2];
        double[] p3p1 = new double[2];
        double jval  = 0;
        double temp  = 0;
        double ival  = 0;
        double p3p1i = 0;
        double triptx;
        double xval;
        double yval;
        double t1;
        double t2;
        double t3;
        double t;
        double exx;
        double d;
        double eyy;

        //TRANSFORMA OS PONTOS EM VETORES
        //PONTO 1
        P1[0] = point1.getX();
        P1[1] = point1.getY();
        //PONTO 2
        P2[0] = point2.getX();
        P2[1] = point2.getY();
        //PONTO 3
        P3[0] = point3.getX();
        P3[1] = point3.getY();

        //TRANSFORMA O VALOR DE METROS PARA A UNIDADE DO MAPA
        //DISTANCIA ENTRE O PONTO 1 E A MINHA LOCALIZACAO
        distance1 = (distance1 / 100000);
        //DISTANCIA ENTRE O PONTO 2 E A MINHA LOCALIZACAO
        distance2 = (distance2 / 100000);
        //DISTANCIA ENTRE O PONTO 3 E A MINHA LOCALIZACAO
        distance3 = (distance3 / 100000);

        for (int i = 0; i < P1.length; i++) {
            t1   = P2[i];
            t2   = P1[i];
            t    = t1 - t2;
            temp += (t*t);
        }
        d = Math.sqrt(temp);
        for (int i = 0; i < P1.length; i++) {
            t1    = P2[i];
            t2    = P1[i];
            exx   = (t1 - t2)/(Math.sqrt(temp));
            ex[i] = exx;
        }
        for (int i = 0; i < P3.length; i++) {
            t1      = P3[i];
            t2      = P1[i];
            t3      = t1 - t2;
            p3p1[i] = t3;
        }
        for (int i = 0; i < ex.length; i++) {
            t1 = ex[i];
            t2 = p3p1[i];
            ival += (t1*t2);
        }
        for (int  i = 0; i < P3.length; i++) {
            t1 = P3[i];
            t2 = P1[i];
            t3 = ex[i] * ival;
            t  = t1 - t2 -t3;
            p3p1i += (t*t);
        }
        for (int i = 0; i < P3.length; i++) {
            t1 = P3[i];
            t2 = P1[i];
            t3 = ex[i] * ival;
            eyy = (t1 - t2 - t3)/Math.sqrt(p3p1i);
            ey[i] = eyy;
        }
        for (int i = 0; i < ey.length; i++) {
            t1 = ey[i];
            t2 = p3p1[i];
            jval += (t1*t2);
        }
        xval = (Math.pow(distance1, 2) - Math.pow(distance2, 2) + Math.pow(d, 2))/(2*d);
        yval = ((Math.pow(distance1, 2) - Math.pow(distance3, 2) + Math.pow(ival, 2) + Math.pow(jval, 2))/(2*jval)) - ((ival/jval)*xval);

        t1 = point1.getX();
        t2 = ex[0] * xval;
        t3 = ey[0] * yval;
        triptx = t1 + t2 + t3;
        result.setX(triptx);
        t1 = point1.getY();
        t2 = ex[1] * xval;
        t3 = ey[1] * yval;
        triptx = t1 + t2 + t3;
        result.setY(triptx);

        return result;
    }

    class CoordPoint {

        private String deviceAddress;
        private double x;
        private double y;

        public CoordPoint(final String deviceAddress) {
            this.deviceAddress = deviceAddress;
        }

        public CoordPoint(final String deviceAddress, final double x, final double y) {
            this.deviceAddress = deviceAddress;
            this.x = x;
            this.y = y;
        }

        public String getDeviceAddress() {
            return deviceAddress;
        }

        public void setDeviceAddress(final String deviceAddress) {
            this.deviceAddress = deviceAddress;
        }

        public double getX() {
            return x;
        }

        public void setX(final double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(final double y) {
            this.y = y;
        }
    }

    /**
     * Contains attributes of received advertisement.
     */
    class Advertisement {

        public String prefix;
        public String uuid;
        public String major;
        public String minor;
        public int txPower;
        public int rssi;

        public Advertisement(byte[] advertisementData, int rssi) {
            prefix = convertByteToHex(Arrays.copyOfRange(advertisementData, 0, 9));
            uuid = convertByteToHex(Arrays.copyOfRange(advertisementData, 9, 25));
            major = convertByteToString(Arrays.copyOfRange(advertisementData, 25, 27));
            minor = convertByteToString(Arrays.copyOfRange(advertisementData, 27, 29));
            txPower = convertByteToInteger(Arrays.copyOfRange(advertisementData, 29, 30));
            if (txPower == 0) {
                throw new IllegalStateException("No iBeacon!");
            }
            this.rssi = rssi;
        }

        @Override
        public String toString() {
            return new StringBuilder().append("prefix: ").append(prefix)
                    .append(", uuid: ").append(uuid)
                    .append(", major: ").append(major)
                    .append(", minor:").append(minor)
                    .append(", txPower: ").append(txPower)
                    .append(", rssi: ").append(rssi)
                    .toString();
        }
    }
}

