package com.coen390.abreath.service;


import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.coen390.abreath.common.Constant;
import com.coen390.abreath.ui.model.DashboardViewModel;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class BleService extends Service {
    private Binder binder = new LocalBinder();;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;
    private MutableLiveData<Float> mBluetoothResults;

    public final static String ACTION_GATT_CONNECTED =
            "coen390.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "coen390.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SUCCESS_DISCOVERED =
            "coen390.bluetooth.le.ACTION_GATT_SUCCESS_DISCOVERED";
    public final static String ACTION_READ_DATA = "coen390.bluetooth.le.ACTION_READ_DATA";

    public final static String BLE_READ_STRING = "coen390.bluetooth.le.BLE_READ_STRING";

    //Get Client Descriptor UUID https://stackoverflow.com/questions/47475431/how-to-find-out-client-characteristic-config
    //Sample Description Class https://github.com/googlearchive/android-BluetoothLeGatt/blob/master/Application/src/main/java/com/example/android/bluetoothlegatt/SampleGattAttributes.java
    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {

        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            if(newState == BluetoothProfile.STATE_CONNECTED){
                broadcastUpdate(ACTION_GATT_CONNECTED);
                bluetoothGatt.discoverServices();
            }else if(newState == BluetoothProfile.STATE_DISCONNECTED){
                broadcastUpdate(ACTION_GATT_DISCONNECTED);
                bluetoothGatt.disconnect();
            }
            super.onConnectionStateChange(gatt, status, newState);

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);

            if(status == BluetoothGatt.GATT_SUCCESS){
                mBluetoothResults.postValue(Float.parseFloat(characteristic.getStringValue(0)));

                broadcastUpdate(ACTION_READ_DATA, characteristic.getStringValue(0));
            }
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            gatt.readCharacteristic(characteristic);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {


            if(status == BluetoothGatt.GATT_SUCCESS){{
                broadcastUpdate(ACTION_GATT_SUCCESS_DISCOVERED);
            }
            }else{
                Log.w("inapp", "On Service Discovered received: "+status);
            }
        }
    };

    public LiveData<Float> getBluetoothResult(){
        return this.mBluetoothResults;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        BluetoothManager manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = manager.getAdapter();

        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {

        this.close();
        return super.onUnbind(intent);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBluetoothResults = new MutableLiveData<>();
    }

    @SuppressLint("MissingPermission")
    public void close(){
        if(bluetoothGatt != null){
            bluetoothGatt.close();
            bluetoothGatt = null;
        }
    }

    @SuppressLint("MissingPermission")
    public void readCharacteristics(BluetoothGattCharacteristic characteristic){
        if(bluetoothGatt == null) return;

        bluetoothGatt.readCharacteristic(characteristic);
    }

    @SuppressLint("MissingPermission")
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic){
        if(bluetoothGatt == null) return;
        bluetoothGatt.setCharacteristicNotification(characteristic, true);

    }

    @SuppressLint("MissingPermission")
    public boolean isBluetoothConnected(){
        if(bluetoothAdapter == null) return false;

        for (BluetoothDevice x : bluetoothAdapter.getBondedDevices()){
            if(x.getName().equals(Constant.BleAttributes.DEVICE_TO_CONNECT)){
                return true;
            }
        }
        return false;
    }

    public boolean isBleSupported(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter != null;
    }

    public boolean isBleEnabled(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter.isEnabled();
    }

    @SuppressLint("MissingPermission")
    public boolean connectTo(final String address){
        if(bluetoothAdapter == null || address == null) return false;
        try {
            final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);

            bluetoothGatt = device.connectGatt(this, true, bluetoothGattCallback);

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

    private void broadcastUpdate(final String action, final String payload){
        final Intent intent = new Intent(action);
        intent.putExtra(BLE_READ_STRING, payload);
        sendBroadcast(intent);
    }

    public class LocalBinder extends Binder{
        public BleService getService(){
            return BleService.this;
        }
    }
}
