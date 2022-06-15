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
import android.content.SharedPreferences;
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
import android.os.Looper;
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
import com.coen390.abreath.service.BluetoothServiceConnection;
import com.coen390.abreath.service.GattBroadcastReceiver;

import java.util.regex.Pattern;


/**
 * Initial screen allowing user to perform bluetooth connection with the remote device
 * Start button is used to initiate the bluetooth service and show the current status of the ble scanning.
 */
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


    @SuppressLint("MissingPermission")
    private ActivityResultLauncher<IntentSenderRequest> startBluetoothActivityForResult;

    public ConnectionDashboard() {}

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        SharedPreferences frag = getActivity().getSharedPreferences("whichfrag", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = frag.edit();
        editor.putString("fragment", "bluetooth");
        editor.apply();

        /* *
         * Defines behaviour on bluetooth connection
         */
        serviceConnection = new BluetoothServiceConnection(new BluetoothServiceConnection.onBleService() {
            @Override
            public void onConnected(BleService bleService) {
                /*
                Assigns the Ble Service object and set the connect state
                 */
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

        /* *
         * Registers callback when is receives pop up for enabling bluetooth
         */
        startSystemBluetooth = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

            switch(result.getResultCode()){
                case Activity.RESULT_OK:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermission();
                    }
                    /*
                    Immediately start scanning once bluetooth has been enabled
                     */
                    startBluetoothDiscovery();
                    break;
                case Activity.RESULT_CANCELED:
                    /*
                    User has not allowed enabling of the bluetooth
                     */
                    Log.d("inapp - Req Permission", "Permission denied by user");
                default:
                    break;
            }
        });

        /*
        Register callback when bluetooth is first pairing with the remote device
         */
        startBluetoothActivityForResult = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {
            switch(result.getResultCode()){
                case Activity.RESULT_OK:
                    if(result.getData() != null){
                        BluetoothDevice device = result.getData().getParcelableExtra(CompanionDeviceManager.EXTRA_DEVICE);
                        if(device != null){
                            //Note that in case of disconnected remote device, use case should be handled on the dashboard page
                            device.createBond();
                            //Device has paired
                            //Start ble connection
                            bluetoothService.connectTo(device.getAddress());
                        }
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    isConnected = false;
                    toggleProgressBar();
                    break;
                default:
                    break;
            }
        });
        /*
        Action to perform when fragment is notified from the BleService of connection state change
         */
        gattUpdateReceiver = new GattBroadcastReceiver(new GattBroadcastReceiver.GattBroadcastReceiverListener() {
            @Override
            public void onActionConnected(Context context) {
                Toast.makeText(context, "Connected", Toast.LENGTH_LONG).show();

                isConnected = true;
                toggleProgressBar();
            }

            @Override
            public void onActionDisconnected(Context context) {
                Log.d(TAG, "disconnected");
                Toast.makeText(context, "Disconnected", Toast.LENGTH_LONG).show();
                isConnected = false;
            }

            @Override
            public void onActionWriteData(Context context) {
                Navigation.findNavController(requireView()).navigate(R.id.action_connectionDashboard_to_navigation_dashboard);

            }

            @Override
            public void onActionDiscovered(Context context) {
                /*
                Once service has been assigned, start immediately sending data to the remote device to notify connection has been established
                 */
                bluetoothService.setCharacteristicsGattServices(Constant.BleAttributes.ABREATH_SERVICE_UUID, Constant.BleAttributes.ABREATH_SENSOR_CHARACTERISTICS_UUID);
                bluetoothService.writeCharacteristic("M");
            }

            @Override
            public void onActionBondChanged(Context context, int status) {
                switch(status){
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
            }
        });

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentConnectionDashboardBinding.inflate(inflater, container, false);
        connect_button = binding.button;
        spin_progress = binding.dashboardProgressBar;
        mHandlerConnection = new Handler(Looper.getMainLooper());

        connect_button.setOnClickListener(view -> {
            if(!bluetoothService.isBleSupported() || !bluetoothService.isBleEnabled()){
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                /*
                If no bluetooth connection is not enabled, request user to allow app to enable bluetooth
                 */
                startSystemBluetooth.launch(intent);
            }else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //Request any required permissions if app does not have those
                    requestPermission();
                }
                //Otherwise, initiates the Ble scanning
                startBluetoothDiscovery();
            }
        });

        return binding.getRoot();
    }

    /**
     * Toggle SpinKit progress bar according to the connection state
     * Sets text value accordingly
     */
    private void toggleProgressBar(){
        isScanning = !isScanning;
        if(isScanning){
            spin_progress.setVisibility(View.VISIBLE);
            connect_button.setEnabled(false);
            connect_button.setText(R.string.connecting);
        }else{
            spin_progress.setVisibility(View.GONE);
            connect_button.setEnabled(true);
            mHandlerConnection.removeCallbacksAndMessages(null);
            connect_button.setText(isConnected ? R.string.connected : R.string.start );
        }
    }


    /**
     * Initiates the bluetooth scanning and sets the connection state accordingly
     * Note that a postponed handler is set to stop scanning if no bluetooth device is found within 12 seconds
     * Note that the bluetooth scanning is managed by the Companion library provided by Android
     * This implementation follows the guidelines provided by the Android documentation
     */
    @SuppressLint("MissingPermission")
    private void startBluetoothDiscovery(){
        toggleProgressBar();
        mHandlerConnection.postDelayed(() -> {
            if(isScanning){
                spin_progress.setVisibility(View.GONE);
                connect_button.setEnabled(true);
                connect_button.setText(R.string.start);
                isScanning = false;
                bluetoothService.close();
                Log.e("Ble Handler", "Run out of time");
            }
        }, 12000);

        /*
        Filter to allow companion to find only the known device
         */
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


    /**
     * Request user for permission to allow app to perform bluetooth scanning and connection with a remote device
     */
    private void requestPermission() {
        int permissionCheck = requireActivity().checkSelfPermission("Manifest.permission.BLUETOOTH_CONNECT");
        permissionCheck += requireActivity().checkSelfPermission("Manifest.permission.BLUETOOTH_SCAN");
        if (permissionCheck != 0) {
            /*
            Permissions required by this application to connect with remote device
             */
            requireActivity().requestPermissions(new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT}, 1001); //Any number
            Log.d("BLuetooth", "PERMISSION");

        }
    }


    /*
    LIFECYCLE HOOKS
     */

    @Override
    public void onResume() {
        super.onResume();




    }

    @Override
    public void onStart() {
        super.onStart();
        //Starts remote service that will handle the communication & transferring of data between device and app
        //implementation depends on design and protocol chosen for communication
        Intent intent = new Intent(getContext(), BleService.class);
        requireActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        /*
        Defines the actions this fragment can receive from the BleService
         */
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BleService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BleService.ACTION_GATT_SUCCESS_DISCOVERED);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BleService.ACTION_WRITE_DATA);
        requireContext().registerReceiver(gattUpdateReceiver, intentFilter);
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