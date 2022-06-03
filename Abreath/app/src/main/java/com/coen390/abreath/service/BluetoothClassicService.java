package com.coen390.abreath.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.UUID;


public class BluetoothClassicService extends Service {
    private Binder binder = new LocalBinder();
    private BluetoothAdapter mBluetoothAdapter;
    private static UUID APP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ConnThread mConnThread;
    private int SERVICE_STATE;

    private static final int STATE_INIT = 0;
    private static final int STATE_CONN = 1;
    private static final int STATE_CONNECTED = 2;
    private static final int STATE_LISTENING = 3;



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        SERVICE_STATE = STATE_INIT;


        return binder;
    }

    public boolean isBluetoothSupported(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter != null;
    }


    public synchronized void connect(BluetoothDevice device){
        if(SERVICE_STATE == STATE_CONN){
            if(mConnThread != null){
                mConnThread.cancel();
                mConnThread = null;
            }
        }
        mConnThread = new ConnThread(device);
        mConnThread.start();
        SERVICE_STATE = STATE_CONN;
    }


    private void onConnectionFailed(){
        Log.e("BluetoothClassicService", "Sok conn failed");
    }
    private void onConnectionSuccess(BluetoothDevice device, BluetoothSocket socket){
        if(mConnThread != null){
            mConnThread.cancel();
            mConnThread = null;
        }

        SERVICE_STATE = STATE_CONNECTED;
        Log.i("BluetoothClassicService", "Connection established");
    }


    public class LocalBinder extends Binder{
        public BluetoothClassicService getService(){return BluetoothClassicService.this; }
    }


    private class ConnThread extends Thread{
        private final BluetoothDevice mDevice;
        private final BluetoothSocket mSocket;
        @SuppressLint("MissingPermission")
        public ConnThread(BluetoothDevice device){
            this.mDevice = device;
            BluetoothSocket tmp = null;
            try {
                tmp = device.createRfcommSocketToServiceRecord(APP_UUID);
            }catch (IOException e){
                Log.e("ConnThread", "Socket's create method failed", e);
            }

            mSocket = tmp;
        }

        @SuppressLint("MissingPermission")
        public void run(){
            Log.i("ConnSocket", "Attempting to connect socket");

            mBluetoothAdapter.cancelDiscovery();
            if(!mBluetoothAdapter.isEnabled()) mBluetoothAdapter.enable();

            try{
                mSocket.connect();
            }catch (IOException e){
                Log.e("ConnSocket", "unable to connect socket", e);
                try{
                    mSocket.close();
                }catch (IOException e_sok){
                    Log.e("ConnSocket", "unable to close socket", e_sok);
                }
                onConnectionFailed();
                return;
            }
            Log.i("BluetoothClassicService", "Connection established");

            //Reset this thread from the service
            synchronized (BluetoothClassicService.this){
                mConnThread = null;
            }
            onConnectionSuccess(mDevice, mSocket);
        }

        public void cancel() {
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.e("ConnThread", "Socket's close method failed", e);
            }
        }
    }
}