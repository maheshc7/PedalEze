package com.smazee.product.pedaleze;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.betomaluje.miband.ActionCallback;
import com.betomaluje.miband.MiBand;
import com.betomaluje.miband.model.BatteryInfo;
import com.ncorti.slidetoact.SlideToActView;
import com.smazee.product.pedaleze.model.MessageSender;
import com.smazee.product.pedaleze.model.ProfileDetails;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static android.bluetooth.BluetoothAdapter.STATE_CONNECTED;

public class ProfileActivity extends AppCompatActivity {
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bleScanner;
    private BluetoothGatt bleGatt;
    private BluetoothDevice device , myDevice;
    private BluetoothGatt gatt;
    private Handler mhandler;
    public  MapsActivity mapsActivity;
    private boolean mScanning;
    private int REQUEST_ENABLE_BT;
    public static BluetoothSocket temp;
    Boolean isListeningHeartRate = false;
    Button connect_buton , bat_but , heart_but ;
    BluetoothGatt bluetoothGatt;
    BluetoothDevice bluetoothDevice;
    String serviceUUID;
    PrefManager prefManager;
    TextView map_heart_rate;
    private static final long SCAN_PERIOD = 10000;
    TextView bmi_txt,height_txt,weight_txt,bmi_index,heart_rate_text;
//    private static final UUID UUID_Service = UUID.fromString("19fc95c0c11111e399040002a5d5c51b");
//    private static final UUID UUID_characteristic = UUID.fromString("21fac9e0c11111e392460002a5d5c51b");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
//         mapsActivity = (MapsActivity)getApplicationContext();

        mhandler = new Handler();
connect_buton = (Button)findViewById(R.id.profile_menu_btn);
//bat_but = (Button)findViewById(R.id.battery);
//heart_but = (Button)findViewById(R.id.heart);
        bmi_txt = findViewById(R.id.profile_bmi);
        bmi_index = findViewById(R.id.profile_bmi2);
        height_txt = findViewById(R.id.profile_height);
        weight_txt = findViewById(R.id.profile_weight);
        heart_rate_text = findViewById(R.id.heart_rate_dynamic);

        prefManager = new PrefManager(this);
//        MessageSender messageSender = new MessageSender(ProfileActivity.this);
//        messageSender.getLogin(ProfileActivity.this,prefManager.getPhoneNumber(),"test");
        final SlideToActView swipeBtn = findViewById(R.id.swipe_btn);
        swipeBtn.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideToActView slideToActView) {

                Intent toMap = new Intent(ProfileActivity.this,MapsActivity.class);
                startActivity(toMap);

            }
        });


        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.d("Suppport", "Yes");
            Toast.makeText(this, "Not Supported", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Log.d("Suppport", "Yes");
            Toast.makeText(this, " Supported", Toast.LENGTH_SHORT).show();

        }
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, " No Bluetooth", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, " Bluetooth", Toast.LENGTH_SHORT).show();
            if (!bluetoothAdapter.isEnabled()) {
                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
            }

        }

        connect_buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connect();
            }
        });
//        bat_but.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getBatteryStatus();
//            }
//        });
//        heart_but.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

    }

    public void updateView(ProfileDetails prof){
        height_txt.setText(prof.getHeigh());
        weight_txt.setText(prof.getWeight());
        int bmi = (int)(Integer.parseInt(prof.getWeight())/(Math.pow(Double.parseDouble(prof.getHeigh())/100,2)));
        bmi_txt.setText(Double.toString(bmi));
        if(bmi<=18)
            bmi_index.setText("Underweight");
        else if(bmi>=25)
            bmi_index.setText("Overweight");
        else
            bmi_index.setText("Normal");

    }
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mhandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    bluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            bluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            bluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }


    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           String device_name = device.getName();
                           String device_address = device.getAddress();

                        Log.v("Data","name:"+device_name+"address:"+device_address);
//

                        }
                    });
                }
            };

    void stateConnected() {
        bluetoothGatt.discoverServices();
//        txtState.setText("Connected");
//        Toast.makeText(this,"Connected",Toast.LENGTH_SHORT).show();
        Log.v("Status","Connected");
        startScanHeartRate();
        listenHeartRate();
    }

    void stateDisconnected() {
        bluetoothGatt.disconnect();
//        txtState.setText("Disconnected");
//        Toast.makeText(this,"Disconnected",Toast.LENGTH_SHORT).show();
        Log.d("Status","Disconnected");
    }

public void connect(){

//    MI Band MAC
//    bluetoothDevice = bluetoothAdapter.getRemoteDevice("DB:A9:1E:35:1E:43");
    bluetoothDevice = bluetoothAdapter.getRemoteDevice("E1:5A:B5:72:7F:9A"); // i6HRc MAC
    Log.v("test", "Connecting to " + "i6HRc Band"); // i6HRc
//    Mi Band
//    Log.v("test", "Connecting to " + "Mi Band");
                            Log.v("test", "Device name " + bluetoothDevice.getName());

//    serviceUUID = bluetoothDevice.getUuids().toString();
                            bluetoothGatt = bluetoothDevice.connectGatt(getApplicationContext(), true, bluetoothGattCallback);

//    MiBand miBand = MiBand.getInstance(ProfileActivity.this);
//    if (!miBand.isConnected()) {
//        miBand.connect(new ActionCallback() {
//            @Override
//            public void onSuccess(Object data) {
//                Log.d("status", "Connected with Mi Band!");
//                //show SnackBar/Toast or something
//            }
//
//            @Override
//            public void onFail(int errorCode, String msg) {
//                Log.d("status", "Connection failed: " + msg);
//            }
//        });
//    } else {
//        miBand.disconnect();
//    }


//    miBand.getBatteryInfo(new ActionCallback() {
//        @Override
//        public void onSuccess(final Object data) {
//            BatteryInfo battery = (BatteryInfo) data;
//            //get the cycle count, the level and other information
//            Log.e("status", "Battery: " + battery.toString());
//        }
//
//        @Override
//        public void onFail(int errorCode, String msg) {
//            Log.e("status", "Fail battery: " + msg);
//        }
//    });
    }
    void startScanHeartRate() {
//        txtByte.setText("...");
//        Toast.makeText(this,"Scanning",Toast.LENGTH_SHORT).show();
        Log.v("Heart","Scanning");
        BluetoothGattCharacteristic bchar = bluetoothGatt.getService(CustomBluetoothProfile.HeartRate.service)
                .getCharacteristic(CustomBluetoothProfile.HeartRate.controlCharacteristic);
        bchar.setValue(new byte[]{21, 2, 1});
        bluetoothGatt.writeCharacteristic(bchar);
    }

    void listenHeartRate() {
        Log.v("Heart","Scanning");
        BluetoothGattCharacteristic bchar = bluetoothGatt.getService(CustomBluetoothProfile.HeartRate.service)
                .getCharacteristic(CustomBluetoothProfile.HeartRate.measurementCharacteristic);
        bluetoothGatt.setCharacteristicNotification(bchar, true);
        BluetoothGattDescriptor descriptor = bchar.getDescriptor(CustomBluetoothProfile.HeartRate.descriptor);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        bluetoothGatt.writeDescriptor(descriptor);
        isListeningHeartRate = true;
    }

    void getBatteryStatus() {
//        txtByte.setText("...");
//        Toast.makeText(this,"Battery",Toast.LENGTH_SHORT).show();
        Log.v("Status","Battery Scan");
        BluetoothGattCharacteristic bchar = bluetoothGatt.getService(CustomBluetoothProfile.Basic.service)
                .getCharacteristic(CustomBluetoothProfile.Basic.batteryCharacteristic);
        if (!bluetoothGatt.readCharacteristic(bchar)) {
//            Toast.makeText(this, "Failed get battery info", Toast.LENGTH_SHORT).show();
            Log.v("Battery","Cant Read");
        }else{
            String c = String.valueOf(bchar);
            Log.v("Status",c);
        }



//            BluetoothGattService mBluetoothGattService = gatt.getService(UUID.fromString(serviceUUID));
//            if (mBluetoothGattService != null) {
//                Log.v("service", "Service characteristic UUID found: " + mBluetoothGattService.getUuid().toString());
//            } else {
//                Log.v("service", "Service characteristic not found for UUID: " + serviceUUID);
//
//            }

    }

    void startVibrate() {
        BluetoothGattCharacteristic bchar = bluetoothGatt.getService(CustomBluetoothProfile.AlertNotification.service)
                .getCharacteristic(CustomBluetoothProfile.AlertNotification.alertCharacteristic);
        bchar.setValue(new byte[]{2});
        if (!bluetoothGatt.writeCharacteristic(bchar)) {
            Toast.makeText(this, "Failed start vibrate", Toast.LENGTH_SHORT).show();
        }
    }

    void stopVibrate() {
        BluetoothGattCharacteristic bchar = bluetoothGatt.getService(CustomBluetoothProfile.AlertNotification.service)
                .getCharacteristic(CustomBluetoothProfile.AlertNotification.alertCharacteristic);
        bchar.setValue(new byte[]{0});
        if (!bluetoothGatt.writeCharacteristic(bchar)) {
            Toast.makeText(this, "Failed stop vibrate", Toast.LENGTH_SHORT).show();
        }
    }

    final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.v("test", "onConnectionStateChange");

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                stateConnected();




            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                stateDisconnected();
            }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Log.v("test", "onServicesDiscovered");
            listenHeartRate();

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.v("test", "onCharacteristicRead");
            byte[] data = characteristic.getValue();
//            txtByte.setText(Arrays.toString(data));
            Log.v("data_read",Arrays.toString(data));
//            heart_rate_text.setText(Arrays.toString(data));
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.v("test", "onCharacteristicWrite");
            byte[] data = characteristic.getValue();
//            txtByte.setText(Arrays.toString(data));
            Log.v("data_read",Arrays.toString(data));
//            heart_rate_text.setText(Arrays.toString(data));
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.v("test", "onCharacteristicChanged");
            final byte[] data = characteristic.getValue();
//            txtByte.setText(Arrays.toString(data));
            Log.v("data_change",Arrays.toString(data));
         runOnUiThread(new Runnable() {
             @Override
             public void run() {
               String  arr;
               byte b;

               arr = Arrays.toString(data);
                 heart_rate_text.setText(arr);
//                    mapsActivity.heart_rate.setText(arr);
                 Toast.makeText(getApplicationContext(), Arrays.toString(data), Toast.LENGTH_LONG).show();
             }
         });


                  }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            Log.v("test", "onDescriptorRead");
            byte[] data = descriptor.getValue();
//            txtByte.setText(Arrays.toString(data));
            Log.v("data_read",Arrays.toString(data));
//            heart_rate_text.setText(Arrays.toString(data));
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            Log.v("test", "onDescriptorWrite");
            byte[] data = descriptor.getValue();
//            txtByte.setText(Arrays.toString(data));
            Log.v("data_read",Arrays.toString(data));
//            heart_rate_text.setText(Arrays.toString(data));
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
            Log.v("test", "onReliableWriteCompleted");
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            Log.v("test", "onReadRemoteRssi");
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            Log.v("test", "onMtuChanged");
        }

    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
       return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.item1:
                // Single menu item is selected do something
                // Ex: launching new activity/screen or show alert message
//                Toast.makeText(AndroidMenusActivity.this, "Bookmark is Selected", Toast.LENGTH_SHORT).show();
               connect();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
