package com.coen390.abreath;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.companion.AssociationRequest;
import android.companion.BluetoothDeviceFilter;
import android.companion.CompanionDeviceManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.coen390.abreath.service.BleService;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

public class SensorActivity extends AppCompatActivity {
    private TextView sensorReading, sensorStatus;
    private Button scan;
    private ProgressBar progressBar;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private static final String TAG = "com.coen390.abreath.BLUETOOTH";
    private static final int REQUEST_CODE = 71567;
    private static final int REQUEST_CODE_CONNECTION = 4352;
    private static final String DEVICE_NAME = "com.coen390.abreath.SENSOR";
    private BluetoothAdapter mBluetoothAdapter;

    private List<BluetoothDevice> mDevices;

    private List<String> mDeviceNames;

    private BleService bluetoothService;
    //https://developer.android.com/guide/topics/connectivity/bluetooth/connect-gatt-server
    //https://developer.android.com/guide/topics/connectivity/bluetooth/transfer-ble-data#notification
    //https://www.youtube.com/watch?v=x1y4tEHDwk0
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d("Ble inapp", "running");

            bluetoothService = ((BleService.LocalBinder) iBinder ).getService();
            if(bluetoothService == null || bluetoothService.isBleSupported()){
                //unable to connect

            }
            bluetoothService.connectTo(mDevices.size() > 0 ? mDevices.get(0).getAddress() : null);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d("Ble inapp", "running");
            bluetoothService = null;
        }
    };

    private final BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(BleService.ACTION_GATT_CONNECTED.equals(action)){
                Log.d("Activity inapp", "connected");
            }else if(BleService.ACTION_GATT_DISCONNECTED.equals(action)){
                Log.d("Activity inapp", "disconnected");
            }else if(BleService.ACTION_GATT_SUCCESS_DISCOVERED.equals(action)){
                displayGattServices(bluetoothService.getSupportedGattServices());
            }
        }
    };



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        setProgressBarIndeterminate(true);



        mDevices = new ArrayList<>();
        mDeviceNames = new ArrayList<>();
        sensorReading = findViewById(R.id.sensorReading);
        sensorStatus = findViewById(R.id.statusBluetooth);
        scan = findViewById(R.id.sensorScan);
        listView = findViewById(R.id.listOfBle);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mDeviceNames);
        listView.setAdapter(adapter);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        BluetoothManager manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = manager.getAdapter();

        @SuppressLint("MissingPermission") Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        for(BluetoothDevice x : pairedDevices){
            try{
                Method m = x.getClass().getMethod("removeBond", (Class[]) null);
                m.invoke(x, (Object[]) null);
            }catch(Exception e){
                Log.e("inapp", "error calling removeBond");
            }
        }





//        https://developer.android.com/guide/topics/connectivity/companion-device-pairing
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            BluetoothDeviceFilter  deviceFilter = new BluetoothDeviceFilter.Builder().build();

            AssociationRequest pairingRequest = new AssociationRequest.Builder().addDeviceFilter(deviceFilter).setSingleDevice(false).build();

            CompanionDeviceManager deviceManager = (CompanionDeviceManager) getSystemService(Context.COMPANION_DEVICE_SERVICE);


            deviceManager.associate(pairingRequest, new CompanionDeviceManager.Callback() {
                @Override
                public void onDeviceFound(IntentSender intentSender) {
                    try {
                        Log.d("inapp", deviceManager.getAssociations().toString());
                        startIntentSenderForResult(
                                intentSender, REQUEST_CODE_CONNECTION, null, 0, 0, 0
                        );

                    } catch (IntentSender.SendIntentException e) {
                        Log.e("error on intent", e.toString());
                    }
                }

                @Override
                public void onFailure(CharSequence charSequence) {

                }
            }, null);

        }


        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gattService = new Intent(SensorActivity.this, BleService.class);
                bindService(gattService, serviceConnection, Context.BIND_AUTO_CREATE);
            }
        });




    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                BluetoothDevice deviceToPair = data.getParcelableExtra(CompanionDeviceManager.EXTRA_DEVICE);

                if (deviceToPair != null) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {}
//                    deviceToPair.createBond();
//                    mDeviceNames.add(deviceToPair.getAddress());
                }
            }
        }else if(requestCode == REQUEST_CODE_CONNECTION){
            if(resultCode == Activity.RESULT_OK && data != null){
                BluetoothDevice deviceToPair = data.getParcelableExtra(CompanionDeviceManager.EXTRA_DEVICE);

                if (deviceToPair != null) {

                    mDevices.add(deviceToPair);
                }

            }
        }

        else
            super.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableIntent);
            return;
        }

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "No LE Support for this device", Toast.LENGTH_LONG).show();
            finish();
        }

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BleService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BleService.ACTION_GATT_SUCCESS_DISCOVERED);
        registerReceiver(gattUpdateReceiver, intentFilter);
    }

    private void displayGattServices(List<BluetoothGattService> services){
        if(services == null) return;
        for(BluetoothGattService x : services){
            String uuid = x.getUuid().toString();

            mDeviceNames.add(
                    String.format(Locale.CANADA, "UUID - %s", uuid)
            );
            if(x.getCharacteristic(x.getUuid()) == null) continue;
            for(BluetoothGattDescriptor y : x.getCharacteristic(x.getUuid()).getDescriptors()) {
                mDeviceNames.add(
                        String.format(Locale.CANADA, "Characteristic - %s & UUID - %s", y.toString(), y.getUuid().toString())
                );
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("inapp", "onStop Activity");
        if(bluetoothService != null)
            unbindService(serviceConnection);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}