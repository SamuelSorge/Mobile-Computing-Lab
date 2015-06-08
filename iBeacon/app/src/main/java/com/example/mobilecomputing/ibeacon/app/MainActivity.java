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

public class MainActivity extends Activity {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 10000;

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
                            System.out.println("device: " + device.getName());
                            System.out.println("device address: " + device.getAddress());
                            Advertisment advertisment = new Advertisment(scanRecord);
                            System.out.println("scan record: " + scanRecord);
                            System.out.println(String.valueOf(scanRecord));
                            System.out.println("rssi: "+rssi);
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
    }

    public void scanDevices(View view) {
        // Stops scanning after a pre-defined scan period.
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScanning = false;
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }, SCAN_PERIOD);

        mScanning = true;
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }

    class Advertisment {

        public String prefix;
        public String uuid;
        public String major;
        public String minor;
        public int txPower;

        public Advertisment(byte[] advertisementData) {
            for (int i = 0; i < advertisementData.length; i++) {
                System.out.println("data: " + String.valueOf(advertisementData[i]));
                if (i < 9) {
                    prefix += String.valueOf(advertisementData[i]);
                }
                if (i >= 9 && i < 25) {
                    uuid += String.valueOf(advertisementData[i]);
                }
                if (i >= 25 && i < 27) {
                    major += String.valueOf(advertisementData[i]);
                }
                if (i >= 27 && i < 29) {
                    minor += String.valueOf(advertisementData[i]);
                }
                if (i >= 29 && i < 31) {
                    int number = Integer.parseInt(String.valueOf(advertisementData[i]));
                    System.out.println(number);
                }
            }
        }
    }
}
