package com.coen390.abreath.ui.dashboard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.companion.AssociationRequest;
import android.companion.BluetoothDeviceFilter;
import android.companion.CompanionDeviceManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.coen390.abreath.R;
import com.coen390.abreath.common.Constant;
import com.coen390.abreath.databinding.FragmentConnectionDashboardBinding;
import com.coen390.abreath.service.BleService;
import com.coen390.abreath.service.BluetoothClassicService;
import com.coen390.abreath.service.BluetoothServiceConnection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;


@RequiresApi(api = Build.VERSION_CODES.O)
public class ConnectionDashboard extends Fragment {
    private static final String TAG = "debug.abreath.BLUETOOTH";

    private FragmentConnectionDashboardBinding binding;
    private Button connect_button;
    private ProgressBar spin_progress;
    private Boolean isScanning = false;
    private Boolean isConnected = false;
    private Handler mHandlerConnection;

    private BleService bluetoothService;

    private BluetoothServiceConnection serviceConnection;

    private BroadcastReceiver gattUpdateReceiver;


    private ActivityResultLauncher<Intent> startSystemBluetooth;

    //TODO To be changed
    private BluetoothGattCharacteristic mCharacteristic;

@SuppressLint("MissingPermission")
private ActivityResultLauncher<IntentSenderRequest> startBluetoothActivityForResult;

    public ConnectionDashboard() {}

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        serviceConnection = new BluetoothServiceConnection(new BluetoothServiceConnection.onBleService() {
            @Override
            public void onConnected(BleService bleService) {
                bluetoothService = bleService;
                if(bluetoothService.isBluetoothConnected()){
                    isConnected = true;
                }
            }
            @Override
            public void onDisconnected(ComponentName componentName) {
                bluetoothService = null;
            }
        });

        startSystemBluetooth = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Log.i("inapp Activity", result.toString());

            switch(result.getResultCode()){
                case Activity.RESULT_OK:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermission();
                    }
                    startBluetoothDiscovery();
                    break;
                case Activity.RESULT_CANCELED:
                    Log.d("inapp - Req Permission", "Permission denied by user");
                default:
                    break;
            }
        });

        startBluetoothActivityForResult = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {
            switch(result.getResultCode()){
                case Activity.RESULT_OK:
                    if(result.getData() != null){
                        BluetoothDevice device = result.getData().getParcelableExtra(CompanionDeviceManager.EXTRA_DEVICE);
                        if(device != null){
                            //Note that in case of disconnected remote device, use case should be handled on the dashboard page
                            device.createBond();
                            bluetoothService.connectTo(device.getAddress());
                        }
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    toggleProgressBar();
                    break;
                default:
                    break;
            }
        });

        gattUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                if(BleService.ACTION_GATT_CONNECTED.equals(action)){
                    Log.d(TAG, "connected");

                    Toast.makeText(context, "Connected", Toast.LENGTH_LONG).show();
                    isConnected = true;
                    toggleProgressBar();
                    Navigation.findNavController(requireView()).navigate(R.id.action_connectionDashboard_to_navigation_dashboard);

                }else if(BleService.ACTION_GATT_DISCONNECTED.equals(action)){
                    Log.d(TAG, "disconnected");
                    Toast.makeText(context, "Disconnected", Toast.LENGTH_LONG).show();
                    isConnected = false;
                }else if(BleService.ACTION_GATT_SUCCESS_DISCOVERED.equals(action)) {
                    bluetoothService.setCharacteristicsGattServices(Constant.BleAttributes.ABREATH_SERVICE_UUID, Constant.BleAttributes.ABREATH_SENSOR_CHARACTERISTICS_UUID);

//                  bluetoothService.readCharacteristics(characteristic);
//                  bluetoothService.writeCharacteristic(characteristic, "M");



                }
                else if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                    switch(intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR)){
                        case BluetoothDevice.BOND_BONDED:
                            Toast.makeText(context, "Successfully connected to breathalyzer", Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothDevice.BOND_BONDING:
                            isConnected = true;
                        case BluetoothDevice.BOND_NONE:
                            toggleProgressBar();
                        default:
                            break;
                    }
                }else if(action.equals(BleService.ACTION_WRITE_DATA)){
//                    Navigation.findNavController(requireView()).navigate(R.id.action_connectionDashboard_to_navigation_dashboard);
                }
            }
        };

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentConnectionDashboardBinding.inflate(inflater, container, false);
        connect_button = binding.button;
        spin_progress = binding.dashboardProgressBar;
        mHandlerConnection = new Handler();

        connect_button.setOnClickListener(view -> {
            if(!bluetoothService.isBleSupported()){
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startSystemBluetooth.launch(intent);
            }else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermission();
                }
                startBluetoothDiscovery();
            }
        });

        return binding.getRoot();
    }

    private void toggleProgressBar(){
        isScanning = !isScanning;
        mHandlerConnection.removeCallbacksAndMessages(null);
        if(isScanning){
            spin_progress.setVisibility(View.VISIBLE);
            connect_button.setEnabled(false);
            connect_button.setText(R.string.connecting);
        }else{
            spin_progress.setVisibility(View.GONE);
            connect_button.setEnabled(true);

            connect_button.setText(isConnected ? R.string.connected : R.string.start );
        }
    }



    @SuppressLint("MissingPermission")
    private void startBluetoothDiscovery(){
        toggleProgressBar();

        mHandlerConnection.postDelayed(() -> {
            if(isScanning && !isConnected){
                spin_progress.setVisibility(View.GONE);
                connect_button.setEnabled(true);
                connect_button.setText(R.string.start);
                isScanning = false;
                bluetoothService.close();
            }
        }, 12000);

        BluetoothDeviceFilter deviceFilter = new BluetoothDeviceFilter.Builder().setNamePattern(Pattern.compile(Constant.BleAttributes.DEVICE_TO_CONNECT)).build();

        AssociationRequest pairingRequest = new AssociationRequest.Builder().addDeviceFilter(deviceFilter).setSingleDevice(true).build();

        CompanionDeviceManager deviceManager = (CompanionDeviceManager) requireActivity().getSystemService(Context.COMPANION_DEVICE_SERVICE);

        //TODO startObservingDevicePresence

        deviceManager.associate(pairingRequest, new CompanionDeviceManager.Callback() {
            @Override
            public void onDeviceFound(IntentSender intentSender) {
                Log.d(TAG, deviceManager.getAssociations().toString());
                IntentSenderRequest.Builder req = new IntentSenderRequest.Builder(intentSender);
                startBluetoothActivityForResult.launch(req.build());

            }

            @Override
            public void onFailure(CharSequence charSequence) {
                Log.e(TAG, "Unable to find device - "+charSequence);
            }
        }, null);
    }


    private void requestPermission() {
        int permissionCheck = requireActivity().checkSelfPermission("Manifest.permission.BLUETOOTH_CONNECT");
        permissionCheck += requireActivity().checkSelfPermission("Manifest.permission.BLUETOOTH_SCAN");
        if (permissionCheck != 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requireActivity().requestPermissions(new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT}, 1001); //Any number
            }
        }
    }


    //Lifecycle Hooks
    @Override
    public void onResume() {
        super.onResume();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BleService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BleService.ACTION_GATT_SUCCESS_DISCOVERED);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BleService.ACTION_WRITE_DATA);
        requireContext().registerReceiver(gattUpdateReceiver, intentFilter);

    }

    @Override
    public void onStart() {
        super.onStart();
        //Starts remote service that will handle the communication & transferring of data between device and app
        //implementation depends on design and protocol chosen for communication
        Intent intent = new Intent(getContext(), BleService.class);
        requireActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onStop() {
        super.onStop();
        requireContext().unregisterReceiver(gattUpdateReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(bluetoothService != null)
            requireActivity().unbindService(serviceConnection);

    }
}