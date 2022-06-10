package com.coen390.abreath.service;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.coen390.abreath.MainActivity;

public class NetworkManager {
    private boolean isConnected;
    public ComponentActivity owner;


    public NetworkManager(ComponentActivity activity){
        owner = activity;
    }
    public void checkConnection(ConnectivityManager.NetworkCallback connectionNetworkCallback){
        final ConnectivityManager connectivityManager = (ConnectivityManager) owner.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();
        if(!isConnected){
            Toast.makeText(owner, "Current not connected to internet", Toast.LENGTH_SHORT).show();
            connectivityManager.registerNetworkCallback(new NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build(), connectionNetworkCallback );
            owner.getLifecycle().addObserver(new DefaultLifecycleObserver() {
                @Override
                public void onPause(@NonNull LifecycleOwner owner) {
                    DefaultLifecycleObserver.super.onPause(owner);
                    connectivityManager.unregisterNetworkCallback(connectionNetworkCallback);
                }
            });
        }

    }

    public boolean isConnected(){
        return this.isConnected;
    }

    public static class Builder{
        public static NetworkManager create(ComponentActivity activity){
            return new NetworkManager(activity);
        }
    }
}
