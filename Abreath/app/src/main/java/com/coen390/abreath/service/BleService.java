package com.coen390.abreath.service;


import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.List;

public class BleService extends Service {
    private Binder binder = new LocalBinder();;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;




    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";

    public final static String ACTION_GATT_SUCCESS_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SUCCESS_DISCOVERED";

    //Get Client Descriptor UUID https://stackoverflow.com/questions/47475431/how-to-find-out-client-characteristic-config
    //Sample Description Class https://github.com/googlearchive/android-BluetoothLeGatt/blob/master/Application/src/main/java/com/example/android/bluetoothlegatt/SampleGattAttributes.java
    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            if(newState == BluetoothProfile.STATE_CONNECTED){
                Log.d("ble inapp", "connected");
                broadcastUpdate(ACTION_GATT_CONNECTED);
                bluetoothGatt.discoverServices();
            }else if(newState == BluetoothProfile.STATE_DISCONNECTED){
                Log.d("ble inapp", "disconnected");
                broadcastUpdate(ACTION_GATT_DISCONNECTED);
                bluetoothGatt.disconnect();
            }
            super.onConnectionStateChange(gatt, status, newState);

        }


        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {


            if(status == BluetoothGatt.GATT_SUCCESS){{
                Log.w("inapp", "On Service Discovered");
                broadcastUpdate(ACTION_GATT_SUCCESS_DISCOVERED);
            }
            }else{
                Log.w("inapp", "On Service Discovered received: "+status);
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @SuppressLint("MissingPermission")
    @Override
    public boolean onUnbind(Intent intent) {

        if(bluetoothGatt != null){
            bluetoothGatt.close();
            bluetoothGatt = null;
        }
        return super.onUnbind(intent);

    }

    public boolean isBleSupported(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter != null;
    }

    @SuppressLint("MissingPermission")
    public boolean connectTo(final String address){
        if(bluetoothAdapter == null || address == null) return false;
        try {
            final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
            bluetoothGatt = device.connectGatt(this, false, bluetoothGattCallback);
            return true;
        }catch (IllegalArgumentException e){
            return false;
        }
    }
    public List<BluetoothGattService> getSupportedGattServices(){
        if(bluetoothGatt == null) return null;
        return bluetoothGatt.getServices();
    }

    private void broadcastUpdate(final String action){
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    public class LocalBinder extends Binder{
        public BleService getService(){
            return BleService.this;
        }
    }
}
