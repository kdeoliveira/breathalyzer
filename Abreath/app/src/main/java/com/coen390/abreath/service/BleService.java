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

import com.coen390.abreath.common.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Bluetooth BLE bounded service used for managing and establishing proper BLE connection with a remote device
 * Since the remote device is already known in advance, no need to generify this class
 * This class provides an alternative for Bluetooth Classic, which was initially used to communicate with the esp32
 *
 * NOTE: Some permission checks warnings have been suppressed since they have been properly handled in the Activity
 */
public class BleService extends Service {
    private final Binder binder = new LocalBinder();;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;
    private List<Float> mTempResults;
    private MutableLiveData<List<Float>> mBluetoothResults;
    private MutableLiveData<Boolean> mBluetoothFinished;
    private BluetoothGattCharacteristic mBluetoothCharacteristic;

    public final static String ACTION_GATT_CONNECTED =
            "coen390.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "coen390.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SUCCESS_DISCOVERED =
            "coen390.bluetooth.le.ACTION_GATT_SUCCESS_DISCOVERED";
    public final static String ACTION_READ_DATA = "coen390.bluetooth.le.ACTION_READ_DATA";

    public final static String ACTION_WRITE_DATA = "coen390.bluetooth.le.ACTION_WRITE_DATA";

    public final static String BLE_READ_STRING = "coen390.bluetooth.le.BLE_READ_STRING";

    //Get Client Descriptor UUID https://stackoverflow.com/questions/47475431/how-to-find-out-client-characteristic-config
    //Sample Description Class https://github.com/googlearchive/android-BluetoothLeGatt/blob/master/Application/src/main/java/com/example/android/bluetoothlegatt/SampleGattAttributes.java

    /**
     * Bluetooth Gatt Server state listener
     * Responds to each new state of the Gatt Server
     * Service sends a broadcast intent back to Activities in order to notify state changed
     */
    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {

        /**
         * Action when bluetooth has initially connected to the device
         * Initialize discovery of services
         */
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

        /**
         * Handling of incoming data received by the Ble
         * All incoming data are buffered into a LiveData object that can be accessed by Activities
         *
         * EXPECTED DATA:
         *  (float) [0-9][0-9][0-9].[0-9][0-9] : test results
         *  (float) - 1 : last packet (end of test results)
         */
        @SuppressLint("MissingPermission")
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//            super.onCharacteristicRead(gatt, characteristic, status);
            Log.d("BleService", "onCharacteristicRead");

            if(status == BluetoothGatt.GATT_SUCCESS){
                try{
                    final float input = Float.parseFloat(characteristic.getStringValue(0));
                    Log.d("BleService", Float.toString(input));

                    if(input == -1){
                        mBluetoothFinished.postValue(true);
                    }else{
                        mBluetoothFinished.postValue(false);
                        mTempResults.add(input);
                        mBluetoothResults.postValue(mTempResults);
                    }



                }catch(NumberFormatException e){
                    Log.d("BleService", characteristic.getStringValue(0));
                }
                broadcastUpdate(ACTION_READ_DATA, characteristic.getStringValue(0));
            }
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            gatt.readCharacteristic(characteristic);
            super.onCharacteristicChanged(gatt, characteristic);
        }

        /**
         * Callback used for notified of successful write operation in Ble
         */
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);

            if(status == BluetoothGatt.GATT_SUCCESS){
                broadcastUpdate(ACTION_WRITE_DATA);
            }else{
                Log.e("BleService", "Unable to right into bluetooth");
            }
        }

        /**
         * Callback used for notify activity when bluetooth has been discovered
         */
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

    public LiveData<List<Float>> getBluetoothResult(){
        return this.mBluetoothResults;
    }

    public LiveData<Boolean> getBluetoothFinished(){return this.mBluetoothFinished; }

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
        //Init live data objects only once
        mBluetoothResults = new MutableLiveData<>();
        mBluetoothFinished = new MutableLiveData<>();
        mBluetoothFinished.setValue(false);
        mTempResults = new ArrayList<>();
    }

    /**
     * Close and disconnect bluetooth connection
     */
    @SuppressLint("MissingPermission")
    public void close(){
        Log.d("BleService", "unBind");
        if(bluetoothGatt != null){
            Log.d("BleService", "Closing");
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            bluetoothGatt = null;
        }
        //Resets live data values so they can be reused
        mTempResults.clear();
        mBluetoothResults.setValue(mTempResults);
        mBluetoothFinished.setValue(false);
    }

    /**
     * Provides acces to regular operation performed on the BluetoothGatt class
     */

    @SuppressLint("MissingPermission")
    public void readCharacteristics(BluetoothGattCharacteristic characteristic){
        if(bluetoothGatt == null) return;
        mBluetoothCharacteristic = characteristic;
        bluetoothGatt.readCharacteristic(characteristic);
    }

    @SuppressLint("MissingPermission")
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic){
        if(bluetoothGatt == null) return;
        bluetoothGatt.setCharacteristicNotification(characteristic, true);
    }

    @SuppressLint("MissingPermission")
    public void setCharacteristicNotification(){
        if(bluetoothGatt == null || mBluetoothCharacteristic == null) return;

        bluetoothGatt.setCharacteristicNotification(mBluetoothCharacteristic, true);
    }

    @SuppressLint("MissingPermission")
    public void writeCharacteristic(BluetoothGattCharacteristic characteristic, String value){
        if(bluetoothGatt == null) return;
        Log.d("BleService", "writeCharacteristic");
        characteristic.setValue(value);
        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        bluetoothGatt.writeCharacteristic(characteristic);
    }
    @SuppressLint("MissingPermission")
    public void writeCharacteristic(String value){
        if(bluetoothGatt == null || mBluetoothCharacteristic == null) return;
        Log.d("BleService", "writeCharacteristic");
        mBluetoothCharacteristic.setValue(value);
        mBluetoothCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        bluetoothGatt.writeCharacteristic(mBluetoothCharacteristic);
    }

    /**
     * Check if any device is connected or bonded to this bluetooth adapter
     */
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

    /**
     * Connects a given device via Ble and returns a GattServer object
     * Implementation based on examples provided by the android documentation website
     */
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

    /**
     * Internally sets the right characteristic that will be used during Ble communication according to a known service UUID
     */
    public void setCharacteristicsGattServices(UUID serviceUUID, UUID characteristicUUID){
        if(bluetoothGatt == null) return;

        for(BluetoothGattService x : bluetoothGatt.getServices()){
            if(x.getUuid().equals(serviceUUID)){
                BluetoothGattCharacteristic characteristic = x.getCharacteristic(characteristicUUID);
                if(characteristic != null){
                    mBluetoothCharacteristic = characteristic;
                }
            }
        }
    }

    /**
     * Notifies activities of any new state
     */
    private void broadcastUpdate(final String action){
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    /**
     * Notifies with a payload fof any new state
     */
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
