package com.example.bluetoothcompass;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import processing.android.PFragment;
import processing.android.CompatUtils;
import processing.core.PApplet;

import android.os.Build;
import android.Manifest;

import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private BluetoothGatt bluetoothGatt;
    private Handler handler = new Handler();
    private boolean scanning = false;
    private static final long SCAN_PERIOD = 10000;
    private Sketch sketch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frame = new FrameLayout(this);
        frame.setId(CompatUtils.getUniqueViewId());
        setContentView(frame, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        sketch = new Sketch();
        PFragment fragment = new PFragment(sketch);
        fragment.setView(frame, this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[] {
                                Manifest.permission.BLUETOOTH_SCAN,
                                Manifest.permission.BLUETOOTH_CONNECT
                        }, 1);
                return; // Wait for user to grant permission before continuing
            }
        }

        // BLE setup
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            startScan(); // Start scanning on launch if Bluetooth is available and enabled
        } else {
            System.out.println("Bluetooth not available or not enabled");
            // You may want to prompt the user to enable Bluetooth if not enabled
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1); // Ask the user to enable Bluetooth
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void startScan() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

            // Request the necessary permissions if not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT},
                    1);
            return; // Return early until the user grants permissions
        }

        if (bluetoothLeScanner == null) {
            System.out.println("BluetoothLeScanner is null.");
            return;
        }

        if (!scanning && bluetoothLeScanner != null) {
            handler.postDelayed(() -> {
                scanning = false;
                bluetoothLeScanner.stopScan(leScanCallback);
                System.out.println("Stopped scanning.");
            }, SCAN_PERIOD);

            scanning = true;
            try {
                bluetoothLeScanner.startScan(leScanCallback);
            } catch (SecurityException e) {
                e.printStackTrace();
                System.out.println("Missing BLE permissions.");
            }
            System.out.println("Started BLE scan...");
        }
    }

    private BluetoothGattCharacteristic ledCharacteristic;

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                System.out.println("Connected to device, starting service discovery...");
                gatt.discoverServices(); // Discover services once connected
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                System.out.println("Disconnected from device");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BluetoothGattService service = gatt.getService(UUID.fromString("00001523-1212-efde-1523-785feabcd123"));
                if (service != null) {
                    BluetoothGattCharacteristic magnetometerCharacteristic = service.getCharacteristic(UUID.fromString("00001526-1212-efde-1523-785feabcd123"));
                    if (magnetometerCharacteristic != null) {
                        gatt.setCharacteristicNotification(magnetometerCharacteristic, true); // Enable notifications for this characteristic
                        BluetoothGattDescriptor descriptor = magnetometerCharacteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                        if (descriptor != null) {
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            gatt.writeDescriptor(descriptor); // Write descriptor to enable notifications
                        }
                    }
//                    BluetoothGattCharacteristic writeChar = service.getCharacteristic(UUID.fromString("00001525-1212-efde-1523-785feabcd123"));
//                    if (writeChar != null) {
//                        ledCharacteristic = writeChar;
//                        sketch.setLedCharacteristic(ledCharacteristic, bluetoothGatt); // Pass it to the sketch
//                        System.out.println("Passed led charachteristics to sketch");
//                    }
                }
            } else {
                System.out.println("Service discovery failed");
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            // Handle characteristic value change (notifications)
            if (characteristic.getUuid().toString().equals("00001526-1212-efde-1523-785feabcd123")) {
                int updatedValue = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 0);
                System.out.println("Updated value: " + updatedValue);
                sketch.updateDirection(updatedValue);
                // Update your int parameter based on the new value
            }
        }
    };
    private void connectToDevice(BluetoothDevice device) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            bluetoothGatt = device.connectGatt(this, false, gattCallback);
        } else {
            bluetoothGatt = device.connectGatt(this, false, gattCallback);
        }
        System.out.println("Connecting to " + device.getName() + "...");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothGatt != null) {
            bluetoothGatt.close(); // Always close the GATT connection when done
        }
    }

    private final ScanCallback leScanCallback = new ScanCallback() {
        @RequiresApi(api = Build.VERSION_CODES.S)
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            String name = device.getName();
            String address = device.getAddress();
            if (name == null) name = "Unknown";

            sketch.addDevice(device);

            System.out.println("Found BLE device: " + name + " [" + address + "]");

            if (name.equals("DamjanBLE") && address.equals("F8:9E:71:CB:86:52")) {
                // Stop scanning once the device is found
                bluetoothLeScanner.stopScan(this);
                System.out.println("Found target device, connecting...");

                // Connect to the device
                connectToDevice(device);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            System.out.println("Scan failed with error code: " + errorCode);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (sketch != null) {
            sketch.onRequestPermissionsResult(
                    requestCode, permissions, grantResults);
        }

        if (requestCode == 1 && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            startScan();
        } else {
            System.out.println("Permissions not granted!");
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (sketch != null) {
            sketch.onNewIntent(intent);
        }
    }
}