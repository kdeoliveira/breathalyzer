package com.coen390.abreath.service;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import kotlin.NotImplementedError;

/**
 * Broadcast listener used for notification received by the BleService
 * This is classes is used to decrease the size of function override required when implementing the BroadcastReceiver.
 * Instead, a customized GattBroadcastReceiverListener class is provided so only required function needs to defined.
 */
public class GattBroadcastReceiver extends BroadcastReceiver {
    private final GattBroadcastReceiverListener listener;
    public GattBroadcastReceiver(GattBroadcastReceiverListener listener){
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        switch (action){
            case BleService.ACTION_GATT_CONNECTED:
                listener.onActionConnected(context);
                break;
            case BleService.ACTION_GATT_DISCONNECTED:
                listener.onActionDisconnected(context);
                break;
            case BleService.ACTION_GATT_SUCCESS_DISCOVERED:
                listener.onActionDiscovered(context);
                break;
            case BleService.ACTION_READ_DATA:
                final String payload = intent.getStringExtra(BleService.BLE_READ_STRING);
                listener.onActionReadData(context, payload);
                break;
            case BleService.ACTION_WRITE_DATA:
                listener.onActionWriteData(context);
                break;
            case BluetoothDevice
                        .ACTION_BOND_STATE_CHANGED:
                final int extra = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                listener.onActionBondChanged(context, extra);
            default:
                break;
        }
    }

    /**
     * New BroadcastReceiver definition
     */
    public abstract static class GattBroadcastReceiverListener{

        public abstract void onActionConnected(Context context);
        public abstract void onActionDisconnected(Context context);
        public void onActionDiscovered(Context context){
            throw new NotImplementedError("function not implemented");
        }
        public void onActionBondChanged(Context context, int status){
            throw new NotImplementedError("function not implemented");
        }
        public void onActionWriteData(Context context){
            throw new NotImplementedError("function not implemented");
        }
        public void onActionReadData(Context context, String payload){
            throw new NotImplementedError("function not implemented");
        }
    }
}
