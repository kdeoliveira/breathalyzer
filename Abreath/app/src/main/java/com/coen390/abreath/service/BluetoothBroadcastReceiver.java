//package com.coen390.abreath.service;
//
//import android.bluetooth.BluetoothGattService;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//
//import com.coen390.abreath.common.Constant;
//
//public class BluetoothBroadcastReceiver extends BroadcastReceiver {
//
//    private boolean mIsConnected;
//    private BleService mBleService;
//
//    public BluetoothBroadcastReceiver(BleService bleService){
//        mIsConnected = false;
//        this.mBleService = bleService;
//    }
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        final String action = intent.getAction();
//
//        if(BleService.ACTION_GATT_CONNECTED.equals(action)){
//            mIsConnected = true;
//        }else if(BleService.ACTION_GATT_DISCONNECTED.equals(action)){
//            mIsConnected = false;
//        }else if(BleService.ACTION_GATT_SUCCESS_DISCOVERED.equals(action)){
//            BluetoothGattService gattService = null;
//
//            for(BluetoothGattService x : mBleService.getSupportedGattServices()){
//                if(x.getUuid().equals(Constant.BleAttributes.ABREATH_SERVICE_UUID)){
//                    x.getCharacteristic()
//                    break;
//                }
//            }
//        }
//    }
//}
