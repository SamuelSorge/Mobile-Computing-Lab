package com.example.mobilecomputing.weatherapp;

import android.bluetooth.*;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler = new Handler();
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    private ArrayList<BluetoothDevice> devices = new ArrayList<>();
    private static final long SCAN_PERIOD = 10000;
    private static final int REQUEST_ENABLE_BT = 1; // is this correct?
    private float tempMeasurement = 0f;
    private int humidMeasurement = 0;

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mNewDevicesArrayAdapter.add(device.getName() + "/" + device.getAddress());
                            devices.add(device);
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
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_list, R.id.deviceName);

        ListView newDevicesListView = (ListView) findViewById(R.id.deviceList);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
//            mBluetoothAdapter.startLeScan(new UUID[] {UUID.fromString("00000002-0000-0000-fdfd-fdfdfdfdfdfd")}, mLeScanCallback);
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void connect(View view) {
        // Get the device MAC address, which is the last 17 chars in the View
        String info = ((TextView) view).getText().toString();
        String address = info.substring(info.length() - 17);
        BluetoothDevice targetDevice = getCorrectBluetoothDevice(address);
        if (null == targetDevice) {
            throw new RuntimeException("Wrong device address.");
        }

        // connect to target device by connecting to gatt server (expecting callback)
        targetDevice.connectGatt(this, true, new BluetoothGattCallback() {

            private BluetoothGattCharacteristic temperatureCharacteristic;
            private BluetoothGattCharacteristic humidCharacteristic;
            private BluetoothGatt gatt;

            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                String intentAction;
                // handle connect and disconnect
                if (BluetoothProfile.STATE_CONNECTED == newState) {
                    this.gatt = gatt;
                    System.out.println("Connected to device.");
                    // discover services -> calls onServicesDiscovered if successful
                    System.out.println("Started service discovery: " + gatt.discoverServices());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            findViewById(R.id.connectedTo).setVisibility(View.VISIBLE);
                            findViewById(R.id.showTempButton).setVisibility(View.VISIBLE);
                            findViewById(R.id.showHumidButton).setVisibility(View.VISIBLE);
                        }
                    });
                } else if (BluetoothProfile.STATE_DISCONNECTED == newState) {
                    this.gatt = null;
                    System.out.println("Disconnected to device.");
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                System.out.println("Services discovered.");
                List<BluetoothGattService> services = gatt.getServices();
                System.out.println(services.size() + " services available.");
                BluetoothGattService weatherService = gatt.getService(UUID.fromString("00000002-0000-0000-fdfd-fdfdfdfdfdfd"));
                temperatureCharacteristic = weatherService.getCharacteristics().get(0);
                System.out.println("Using temp. char.: "+temperatureCharacteristic.getUuid().toString());
                this.gatt.readCharacteristic(temperatureCharacteristic);
                humidCharacteristic = weatherService.getCharacteristics().get(1);
                System.out.println("Using humid. char.: "+humidCharacteristic.getUuid().toString());
                this.gatt.readCharacteristic(humidCharacteristic);
                //this.gatt.disconnect();
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                if (null != this.gatt) {
                    if (characteristic == temperatureCharacteristic) {
                        tempMeasurement = readTempMeasurement(characteristic);
                    } else if (characteristic == humidCharacteristic) {
                        humidMeasurement = readHumidMeasurement(characteristic);
                    }
                }
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
            }
        });
    }

    private BluetoothDevice getCorrectBluetoothDevice(String address) {
        for (BluetoothDevice device : devices) {
            if (device.getAddress().equals(address)) {
                return device;
            }
        }
        return null;
    }

    private float readTempMeasurement(BluetoothGattCharacteristic characteristic) {
        if (UUID.fromString("00002a1c-0000-1000-8000-00805f9b34fb").equals(characteristic.getUuid())) {
            return characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_FLOAT, 1);
        }
        return -1;
    }

    private int readHumidMeasurement(BluetoothGattCharacteristic characteristic) {
        if (UUID.fromString("00002a6f-0000-1000-8000-00805f9b34fb").equals(characteristic.getUuid())) {
            return characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 1);
        }
        return -1;
    }

    public void showTemp(View view) {
        TextView tempField = (TextView) findViewById(R.id.tempField);
        tempField.setText("" + tempMeasurement);
        tempField.setVisibility(View.VISIBLE);
    }

    public void showHumid(View view) {
        TextView humidField = (TextView) findViewById(R.id.humidField);
        humidField.setText("" + humidMeasurement);
        humidField.setVisibility(View.VISIBLE);
    }

}
