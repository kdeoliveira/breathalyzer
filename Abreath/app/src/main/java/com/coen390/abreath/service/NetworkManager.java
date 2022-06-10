package com.coen390.abreath.service;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.util.Log;

import androidx.activity.ComponentActivity;

public class NetworkManager {
    public boolean isConnected;
    public ComponentActivity owner;

    public NetworkManager(ComponentActivity activity){
        owner = activity;
    }
    public void checkConnection(ConnectivityManager.NetworkCallback connectionNetworkCallback){
        final ConnectivityManager connectivityManager = (ConnectivityManager) owner.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();
        if(!isConnected){
            Log.d("Main Activity", "No network found still");
            connectivityManager.registerNetworkCallback(new NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build(), connectionNetworkCallback );
        }
    }
}
