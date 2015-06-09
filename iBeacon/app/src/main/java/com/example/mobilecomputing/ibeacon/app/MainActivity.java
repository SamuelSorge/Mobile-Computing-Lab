package com.example.mobilecomputing.ibeacon.app;

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

import java.util.*;

public class MainActivity extends Activity {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 10000;

    private ArrayAdapter<String> mDevicesArrayAdapter;
    private Map<BluetoothDevice, List<Advertisement>> beacons = new HashMap<>(3);
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler = new Handler();
    private boolean mScanning;

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
        for (BluetoothDevice device : beacons.keySet()) {
            List<Advertisement> advertisements = beacons.get(device);
            double distanceSum = 0;
            for (Advertisement ad : advertisements) {
                distanceSum += ad.distance;
            }
            double calculatedDistance = distanceSum / advertisements.size();
            StringBuilder deviceDetails = new StringBuilder();
            deviceDetails.append("device: ").append(device.getName());
            deviceDetails.append(", device address: ").append(device.getAddress());
            deviceDetails.append(", distance: ").append(calculatedDistance);
            deviceDetails.append(", advertisements: ").append(advertisements.size());
            mDevicesArrayAdapter.add(deviceDetails.toString());
        }
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
        return Math.pow(10d, ((double) txPower - rssi) / (10 * 3));
    }

    /**
     * alternative method to get distance.
     * http://stackoverflow.com/questions/20416218/understanding-ibeacon-distancing
     *
     * @param txPower
     * @param rssi
     * @return
     */
    private double calculateAccuracy(int txPower, double rssi) {
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
        public double distance;

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
            distance = getDistance(rssi, txPower);
        }

        @Override
        public String toString() {
            return new StringBuilder().append("prefix: ").append(prefix)
                    .append(", uuid: ").append(uuid)
                    .append(", major: ").append(major)
                    .append(", minor:").append(minor)
                    .append(", txPower: ").append(txPower)
                    .append(", rssi: ").append(rssi)
                    .append(", distance: ").append(distance)
                    .toString();
        }
    }
}
