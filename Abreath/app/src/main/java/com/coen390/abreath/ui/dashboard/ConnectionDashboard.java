package com.coen390.abreath.ui.dashboard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import com.coen390.abreath.databinding.FragmentConnectionDashboardBinding;
import com.coen390.abreath.service.BluetoothClassicService;

import java.util.regex.Pattern;


@RequiresApi(api = Build.VERSION_CODES.O)
public class ConnectionDashboard extends Fragment {

    private FragmentConnectionDashboardBinding binding;
    private Button connect_button;
    private ProgressBar spin_progress;
    private Boolean isScanning = false;
    private Boolean isConnected = false;
    private Handler mHandlerConnection;

    private BluetoothClassicService bluetoothClassicService;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @SuppressLint("MissingPermission")
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            bluetoothClassicService = ((BluetoothClassicService.LocalBinder) iBinder).getService();
            if(!bluetoothClassicService.isBluetoothSupported()){
                Toast.makeText(getContext(), "Bluetooth is not supported in this device", Toast.LENGTH_SHORT).show();
                return;
            }
            else if(bluetoothClassicService.isBluetoothConnected()){
                isConnected = true;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i("Fragment", "Bluetooth Service Stopped");
            bluetoothClassicService = null;
        }
    };
    private final ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
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

@SuppressLint("MissingPermission")
private final ActivityResultLauncher<IntentSenderRequest> startBluetoothActivityForResult = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {
    switch(result.getResultCode()){
        case Activity.RESULT_OK:
            if(result.getData() != null){
                BluetoothDevice device = result.getData().getParcelableExtra(CompanionDeviceManager.EXTRA_DEVICE);
                if(device != null){
                    //Note that in case of disconnected remote device, use case should be handled on the dashboard page
                    device.createBond();
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

    public ConnectionDashboard() {}

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentConnectionDashboardBinding.inflate(inflater, container, false);
        connect_button = binding.button;
        spin_progress = binding.dashboardProgressBar;
        mHandlerConnection = new Handler();


        final IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        requireContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                    switch(intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR)){
                        case BluetoothDevice.BOND_BONDED:
                            toggleProgressBar();
                            Toast.makeText(getContext(), "Successfully connected to breathalyzer", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_connectionDashboard_to_navigation_dashboard);
                            break;
                        case BluetoothDevice.BOND_BONDING:
                            isConnected = true;
                        case BluetoothDevice.BOND_NONE:
                            toggleProgressBar();
                        default:
                            break;
                    }
                }
            }
        }, intentFilter);

        connect_button.setOnClickListener(view -> {
            if(!bluetoothClassicService.isBluetoothEnabled()){
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startForResult.launch(intent);
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
            connect_button.setText(getResources().getText(R.string.connecting));
            //This would be required if using startDiscovery since it only scan for around 12 seconds
//            Handler handler = new Handler();
//            handler.postDelayed(() -> {
//                //Force stop animation after timeout
//                spin_progress.setVisibility(View.GONE);
//                connect_button.setEnabled(true);
//                connect_button.setText(getResources().getText(R.string.start));
//            }, 12000);

        }else{
            spin_progress.setVisibility(View.GONE);
            connect_button.setEnabled(true);

            connect_button.setText(isConnected ? getResources().getText(R.string.connected) : getResources().getText(R.string.start) );
        }
    }



    @SuppressLint("MissingPermission")
    private void startBluetoothDiscovery(){
        toggleProgressBar();

        if(bluetoothClassicService.isBluetoothConnected()){
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_connectionDashboard_to_navigation_dashboard);
            return;
        }

        mHandlerConnection.postDelayed(() -> {
            if(isScanning && !isConnected){
                spin_progress.setVisibility(View.GONE);
                connect_button.setEnabled(true);
                connect_button.setText(R.string.start);
                isScanning = false;
            }
        }, 12000);

        BluetoothDeviceFilter deviceFilter = new BluetoothDeviceFilter.Builder().setNamePattern(Pattern.compile(BluetoothClassicService.deviceNameToConnect)).build();

        AssociationRequest pairingRequest = new AssociationRequest.Builder().addDeviceFilter(deviceFilter).setSingleDevice(true).build();

        CompanionDeviceManager deviceManager = (CompanionDeviceManager) requireActivity().getSystemService(Context.COMPANION_DEVICE_SERVICE);

        //TODO startObservingDevicePresence

        deviceManager.associate(pairingRequest, new CompanionDeviceManager.Callback() {
            @Override
            public void onDeviceFound(IntentSender intentSender) {
                Log.d("inapp", deviceManager.getAssociations().toString());
                IntentSenderRequest.Builder req = new IntentSenderRequest.Builder(intentSender);
                startBluetoothActivityForResult.launch(req.build());
            }

            @Override
            public void onFailure(CharSequence charSequence) {
                Log.e("inapp Bluetooth", "Unable to find device - "+charSequence);
            }
        }, null);
    }


    private void requestPermission() {
        int permissionCheck = requireActivity().checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
        permissionCheck += requireActivity().checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
        if (permissionCheck != 0) {
            requireActivity().requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
        }
    }

    //Lifecycle hooks

    @Override
    public void onResume() {
        super.onResume();
        if(bluetoothClassicService != null && bluetoothClassicService.isBluetoothConnected()){
            isConnected = true;
        }else{
            isConnected = false;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        //Starts remote service that will handle the communication & transferring of data between device and app
        //implementation depends on design and protocol chosen for communication
        Intent intent = new Intent(getContext(), BluetoothClassicService.class);
        requireActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onStop() {
        super.onStop();
        if(bluetoothClassicService != null)
            requireActivity().unbindService(serviceConnection);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();


    }
}