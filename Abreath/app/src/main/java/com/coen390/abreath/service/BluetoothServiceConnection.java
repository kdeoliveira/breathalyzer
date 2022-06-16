package com.coen390.abreath.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

/**
 * Listener implementation for bluetooth connection
 * Implements basic functionalities when bluetooth is connected or disconnected
 */
public class BluetoothServiceConnection implements ServiceConnection {
    private onBleService mOnBleService;
    private final static String TAG ="SERVICE_CONN";

    public BluetoothServiceConnection(onBleService service){
        mOnBleService = service;
    }
    @Override
    public void onBindingDied(ComponentName name) {
        ServiceConnection.super.onBindingDied(name);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        BleService mBluetoothService = ((BleService.LocalBinder) iBinder).getService();
        if(mBluetoothService == null || !mBluetoothService.isBleSupported()){
            Log.e(TAG, "Bluetooth BLE is not supported in this device");
        }else{
            mOnBleService.onConnected(mBluetoothService);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        mOnBleService.onDisconnected(componentName);
    }

    /**
     * Callback interface used when new connection is established or a bluetooth is disconnected
     */
    public interface onBleService{
        void onConnected(BleService bleService);
        void onDisconnected(ComponentName componentName);
    }
}
