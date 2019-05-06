package com.smazee.product.pedaleze;

import android.app.Activity;
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
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
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
import java.util.List;
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
    Intent toMap;
    BluetoothGattCharacteristic bcharacterstic , bandcharacterstic;
    BluetoothGattService bservice , bandservice;
    private boolean mScanning;
    private int REQUEST_ENABLE_BT;
    public static BluetoothSocket temp;
    Boolean isListeningHeartRate = false;
    boolean map=false;
    Button connect_buton , bat_but , heart_but , band_btn ;
    BluetoothGatt bluetoothGatt, Bandgatt;
    BluetoothDevice bluetoothDevice , banddevice;
    String serviceUUID;
    PrefManager prefManager;
    TextView map_heart_rate;
    private static final long SCAN_PERIOD = 10000;
    TextView bmi_txt,height_txt,weight_txt,bmi_index,heart_rate_text,profile_name;
    static ProfileDetails profile;
//    private static final UUID UUID_Service = UUID.fromString("19fc95c0c11111e399040002a5d5c51b");
//    private static final UUID UUID_characteristic = UUID.fromString("21fac9e0c11111e392460002a5d5c51b")
    BluetoothDevice esp32;
    public static int mode_val = 0;
    public static int band_status = 0;
    BluetoothGatt esp32Gatt;
    BluetoothGattCharacteristic esp32GattCharacteristic;
    public static UUID esp32Service = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    public static UUID RXUUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
    public static UUID tempDatasend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toast.makeText(getApplicationContext(), "Welcome to Pedaleze !", Toast.LENGTH_SHORT).show();
//      mapsActivity = (MapsActivity)getApplicationContext();

        mhandler = new Handler();
        connect_buton = findViewById(R.id.profile_menu_btn);
        //bat_but = (Button)findViewById(R.id.battery);
        //heart_but = (Button)findViewById(R.id.heart);
        bmi_txt = findViewById(R.id.profile_bmi);
        bmi_index = findViewById(R.id.profile_bmi2);
        height_txt = findViewById(R.id.profile_height);
        weight_txt = findViewById(R.id.profile_weight);
        heart_rate_text = findViewById(R.id.heart_rate_dynamic);
        profile_name = findViewById(R.id.profile_name);
        band_btn = findViewById(R.id.band_btn);

        toMap = new Intent(ProfileActivity.this,MapsActivity.class);
        prefManager = new PrefManager(this);
        MessageSender messageSender = new MessageSender(ProfileActivity.this);
        messageSender.getLogin(ProfileActivity.this,prefManager.getPhoneNumber(),"test");

        final SlideToActView swipeBtn = findViewById(R.id.swipe_btn);
        swipeBtn.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideToActView slideToActView) {
                //toMap.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(toMap);
                map=true;
                swipeBtn.resetSlider();

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
        band_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connect_band();
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
        mhandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
switch (message.what){
    case 0:Toast.makeText(getApplicationContext(), "Cycle is Connected with the App !", Toast.LENGTH_SHORT).show(); break;
    case 1:Toast.makeText(getApplicationContext(), "Cycle Device got Disconnected with the App !", Toast.LENGTH_SHORT).show();break;
    case 2:Toast.makeText(getApplicationContext(), "Band is Connected with the App !", Toast.LENGTH_SHORT).show();break;
    case 3:Toast.makeText(getApplicationContext(), "Band got Disconnected with the App !", Toast.LENGTH_SHORT).show();break;
    default: Toast.makeText(getApplicationContext(), "Pedaleze | v0.1 | Alpha Demo ", Toast.LENGTH_SHORT).show();break;

}
            }
        };
    }

    public void updateView(ProfileDetails prof){
        profile = prof;
        profile_name.setText(prof.getName());
        if(!prof.getHeigh().isEmpty() && !prof.getWeight().isEmpty()) {
            height_txt.setText(prof.getHeigh());
            weight_txt.setText(prof.getWeight());
            int bmi = (int) (Integer.parseInt(prof.getWeight()) / (Math.pow(Double.parseDouble(prof.getHeigh()) / 100, 2)));
            bmi_txt.setText(Double.toString(bmi));
            if (bmi <= 18)
                bmi_index.setText("Underweight");
            else if (bmi >= 25)
                bmi_index.setText("Overweight");
            else
                bmi_index.setText("Normal");
        }
        toMap.putExtra("phno",prof.getSos_number());
    }

    public void editProfile(View view){
        Intent toDetails = new Intent(ProfileActivity.this,DetailsActivity.class);
        startActivity(toDetails);
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
        band_status = 1;
        Bandgatt.discoverServices();

//        txtState.setText("Connected");
//        Toast.makeText(this,"Connected",Toast.LENGTH_SHORT).show();
        Log.v("Status","Connected");
//        startScanHeartRate();
//        listenHeartRate();
    }

    void esp32Connected(UUID temp1){

        tempDatasend = temp1;
        Log.v("ESP32Connect--->","Connected");
        bservice = bluetoothGatt.getService(temp1);
        if(bservice == null){
            Log.v("Status of Bservice","Null");

        }else{
           bcharacterstic = bservice.getCharacteristic(RXUUID);
           if(bcharacterstic == null){
               Log.v("Status of Bcharactersti","Characterstic not Working");
           }else{
               Log.v("Status of Bcharactersti","Characterstic Working");
               bcharacterstic.setValue("Pedaleze App Connected ");
               bluetoothGatt.writeCharacteristic(bcharacterstic);

           }
        }

    }

    public void sendDatatoHardware(String arr , int mode){
        bservice = bluetoothGatt.getService(tempDatasend);
        bcharacterstic = bservice.getCharacteristic(RXUUID);
        String data = arr+","+mode;
        bcharacterstic.setValue(data);
        bluetoothGatt.writeCharacteristic(bcharacterstic);
    }

    void stateDisconnected() {
        band_status = 0;
        Bandgatt.disconnect();
//        txtState.setText("Disconnected");
//        Toast.makeText(this,"Disconnected",Toast.LENGTH_SHORT).show();
        Log.d("Status","Disconnected");
    }

    public void connect(){
        Toast.makeText(getApplicationContext(), "Connecting to the Pedaleze Cycle ", Toast.LENGTH_SHORT).show();
        //    MI Band MAC
        //bluetoothDevice = bluetoothAdapter.getRemoteDevice("DB:A9:1E:35:1E:43");
        //bluetoothDevice = bluetoothAdapter.getRemoteDevice("CC:50:E3:8C:FF:52"); //   MAC Address of Smazee ESP32
        bluetoothDevice = bluetoothAdapter.getRemoteDevice("3C:71:BF:44:71:D6"); //  MAC Address of Pedaleze Team  ESP32 - 1
        //bluetoothDevice = bluetoothAdapter.getRemoteDevice("3C:71:BF:44:71:C2"); //  MAC Address of Pedaleze Team  ESP32 - 2
        Log.v("test", "Connecting to " + "i6HRc Band"); // i6HRc
        //    Mi Band
        //    Log.v("test", "Connecting to " + "Mi Band");
        Log.v("test", "Device name " + bluetoothDevice.getName());

        //    serviceUUID = bluetoothDevice.getUuids().toString();
        bluetoothGatt = bluetoothDevice.connectGatt(getApplicationContext(), true, esp32GattCallback);


    }

    public void connect_band(){
        Toast.makeText(getApplicationContext(), "Connecting to the Band", Toast.LENGTH_SHORT).show();
//        banddevice = bluetoothAdapter.getRemoteDevice("F9:2D:2A:50:B9:EC"); DB:A9:1E:35:1E:43
        banddevice = bluetoothAdapter.getRemoteDevice("F9:2D:2A:50:B9:EC");
        Log.v("test", "Connecting to " + "i6HRc Band"); // i6HRc
        Log.v("test", "Device name " + banddevice.getName());
        Bandgatt = banddevice.connectGatt(getApplicationContext(),true,bluetoothGattCallback);
    }

    public void connect_band_auto(){
//        Toast.makeText(getApplicationContext(), "Connecting to the Band", Toast.LENGTH_SHORT).show();
//        banddevice = bluetoothAdapter.getRemoteDevice("F9:2D:2A:50:B9:EC"); DB:A9:1E:35:1E:43
        banddevice = bluetoothAdapter.getRemoteDevice("F9:2D:2A:50:B9:EC");
        Log.v("test", "Connecting to " + "i6HRc Band"); // i6HRc
        Log.v("test", "Device name " + banddevice.getName());
        Bandgatt = banddevice.connectGatt(getApplicationContext(),true,bluetoothGattCallback);
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
        BluetoothGattCharacteristic bandchar = Bandgatt.getService(CustomBluetoothProfile.HeartRate.service)
                .getCharacteristic(CustomBluetoothProfile.HeartRate.measurementCharacteristic);
        Bandgatt.setCharacteristicNotification(bandchar, true);
        BluetoothGattDescriptor descriptor = bandchar.getDescriptor(CustomBluetoothProfile.HeartRate.descriptor);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        Bandgatt.writeDescriptor(descriptor);
        isListeningHeartRate = true;
    }

    void bandConnected(UUID temp2){
        Log.v("gg","fdasdfads");
        bandservice = Bandgatt.getService(temp2);
        if(bservice == null){
            Log.v("Status of bandservice","Null");

        }else{
            bandcharacterstic = bandservice.getCharacteristic(CustomBluetoothProfile.HeartRate.measurementCharacteristic);
            if(bandcharacterstic == null){
                Log.v("Status of Bcharactersti","Characterstic not Working");
            }else{
                Log.v("Status of Bcharactersti","Characterstic Working");
                    Bandgatt.setCharacteristicNotification(bandcharacterstic, true);
                BluetoothGattDescriptor descriptor = bandcharacterstic.getDescriptor(CustomBluetoothProfile.HeartRate.descriptor);
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                Bandgatt.writeDescriptor(descriptor);
                isListeningHeartRate = true;

            }
        }
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



    final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.v("test", "onConnectionStateChange");

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Message message = mhandler.obtainMessage(2, "Band Got Connected"); // 2 for Band Connected
                message.sendToTarget();
                stateConnected();
//listenHeartRate();



            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Message message = mhandler.obtainMessage(3, "Band Got Disconnected"); // 3 for Band Disconnected
                message.sendToTarget();
                stateDisconnected();
            }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            Log.v("Band Connect--->", "onServicesDiscovered");
            List<BluetoothGattService> gattServices = gatt.getServices();
            if (status == BluetoothGatt.GATT_SUCCESS) {
                for (BluetoothGattService gattService : gattServices) {
//                    Log.i("uuids", "Service UUID Found: " + gattService.getCharacteristic(esp32Service).toString());
                    Log.i("uuids", "Service UUID Found: " + gattService.getUuid().toString());
                    UUID temp = gattService.getUuid();
                    BluetoothGattService bservice1 = Bandgatt.getService(temp);
                    if(bservice1 == null){
                        Log.v("uuid check","not working");
                    }else
                    {
                        Log.v("uuid check","working");
                        Log.v("uuid temp",temp.toString());
                        Log.v("uuid temddp",CustomBluetoothProfile.HeartRate.service.toString());
                        if(temp.toString().equals(CustomBluetoothProfile.HeartRate.service.toString())){

                            bandConnected(temp);
                        }
                    }
                }
            }else{
                Log.v("status","Didnt Work ");
            }
//            listenHeartRate();



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
                 Log.v("testofsendingdata","done");
               arr = data[1]+"";
                 heart_rate_text.setText(arr);
                    sendDatatoHardware(arr,mode_val);
                 if(esp32GattCharacteristic!=null){
                     Log.v("testofsendingdata","done");
                     esp32GattCharacteristic.setValue("Smazee,a,creative,haven");
                     esp32Gatt.writeCharacteristic(esp32GattCharacteristic);

                 }
                 toMap.putExtra("bpm","dd");
//                    mapsActivity.heart_rate.setText(arr);
                 if(map) {
                     MapsActivity.update(arr);

                 }
//                 Toast.makeText(getApplicationContext(), Arrays.toString(data), Toast.LENGTH_SHORT).show();
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

    public void connectHardware(){
        esp32 = bluetoothAdapter.getRemoteDevice("CC:50:E3:8C:FF:52");
        Log.v("ESP32Connect--->", "Connecting to " + "ESP32 ");
        Log.v("ESP32Connect--->", "Device name " + esp32.getName());
        esp32Gatt= esp32.connectGatt(getApplicationContext(), true, esp32GattCallback);
        esp32GattCharacteristic = esp32Gatt.getService(esp32Service).getCharacteristic(RXUUID);
        esp32Gatt.setCharacteristicNotification(esp32GattCharacteristic,true);
    }

    final BluetoothGattCallback esp32GattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.v("ESP32Connect--->", "onConnectionStateChange");
Log.v("SD",String.valueOf(newState));
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Message message = mhandler.obtainMessage(0, "Cycle got Connected"); // 0 for Cycle Connected
                message.sendToTarget();
                bluetoothGatt.discoverServices();
//                Log.v("ESP32cONNECT--->","SENDING DATA");
//                Log.v("Characteristic ID", bluetoothGatt.getService(esp32Service).getCharacteristics().toString());
//                    BluetoothGattCharacteristic bchar = bluetoothGatt.getService(esp32Service).getCharacteristic(RXUUID);
//                BluetoothGattService bservice = bluetoothGatt.getService(CustomBluetoothProfile.Basic.service);
//if(bservice == null)
//{
//    Log.v("Service Status","Service is not there");
//}else{
//    Log.v("Service Status","Service is there");
//}

//                BluetoothGattCharacteristic bchar = bservice.getCharacteristic(RXUUID);
//                BluetoothGattCharacteristic bchar = bluetoothGatt.getService(esp32Service).getCharacteristic(RXUUID);
//             UUID parcel[] = bluetoothDevice.getUuids();
//                ParcelUuid U[] = bluetoothDevice.getUuids();
//                int len = U.length;
//                Log.v("len",getString(len));
//             Log.v("ESP32cONNECT--->",bchar.toString());
//                esp32GattCallback.onCharacteristicChanged(bluetoothGatt,bchar);
//                bluetoothGatt.setCharacteristicNotification(esp32GattCharacteristic,true);
//                BluetoothGattDescriptor descriptor = bchar.getDescriptor(CustomBluetoothProfile.HeartRate.descriptor);
//                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//                bluetoothGatt.writeDescriptor(descriptor);
//                isListeningHeartRate = true;
            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTED) {

                Message message = mhandler.obtainMessage(1, "Cycle Got Disconnected"); // 1 for Cycle Disconnected
                message.sendToTarget();
                bluetoothGatt.disconnect();

                Log.d("ESP32Connect--->","Disconnected");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Log.v("ESP32Connect--->", "onServicesDiscovered");
            List<BluetoothGattService> gattServices = gatt.getServices();
            if (status == BluetoothGatt.GATT_SUCCESS) {

                for (BluetoothGattService gattService : gattServices) {
//                    Log.i("uuids", "Service UUID Found: " + gattService.getCharacteristic(esp32Service).toString());
                    Log.i("uuids", "Service UUID Found: " + gattService.getUuid().toString());
                    UUID temp = gattService.getUuid();
                    BluetoothGattService bservice1 = bluetoothGatt.getService(temp);
                    if(bservice1 == null){
                        Log.v("uuid check","not working");
                    }else
                    {
                        Log.v("uuid check","working");
                        Log.v("uuid temp",temp.toString());
                        Log.v("uuid temddp",esp32Service.toString());
                        if(temp.toString().equals(esp32Service.toString())){
                            Log.v("gg","fdasdfads");
                            esp32Connected(temp);
                        }
                    }
                }
            }
        }
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.v("ESP32Connect--->", "onCharacteristicWrite");
            byte[] data = characteristic.getValue();
            Log.v("ESP32Connect--->",Arrays.toString(data));
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.v("ESP32Connect--->", "onCharacteristicChanged");
            final byte[] data = characteristic.getValue();
            Log.v("ESP32Connect--->",Arrays.toString(data));
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            Log.v("ESP32Connect--->", "onDescriptorWrite");
            byte[] data = descriptor.getValue();
            Log.v("ESP32Connect--->",Arrays.toString(data));
        }

    };


}
