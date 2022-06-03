package com.coen390.abreath.ui.dashboard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.coen390.abreath.R;
import com.coen390.abreath.databinding.FragmentConnectionDashboardBinding;
import com.coen390.abreath.service.BluetoothClassicService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;


@RequiresApi(api = Build.VERSION_CODES.O)
public class ConnectionDashboard extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private List<BluetoothDevice> mDeviceList;

    private FragmentConnectionDashboardBinding binding;
    private ImageButton connect_button;
    private ProgressBar spin_progress;
    private FrameLayout frameLayout;
    //This must be set through sharedPreferences or background services
    private Boolean isScanning = false;

    private BluetoothClassicService bluetoothClassicService;

    private BluetoothAdapter mBluetoothAdapter;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @SuppressLint("MissingPermission")
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            bluetoothClassicService = ((BluetoothClassicService.LocalBinder) iBinder).getService();


            if(mDeviceList.size() > 0){
                Log.i("Fragment", "Bluetooth Service Started "+mDeviceList.get(0));
                bluetoothClassicService.connect(mDeviceList.get(0));
            }else{
                Log.i("Fragment", "Bluetooth Device not found");

            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i("Fragment", "Bluetooth Service Stopped");

            bluetoothClassicService = null;
        }
    };


@SuppressLint("MissingPermission")
ActivityResultLauncher<IntentSenderRequest> startBluetoothActivityForResult = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {
    switch(result.getResultCode()){
        case Activity.RESULT_OK:
            if(result.getData() != null){
                BluetoothDevice device = result.getData().getParcelableExtra(CompanionDeviceManager.EXTRA_DEVICE);
                if(device != null){
                    device.createBond();
                    toggleProgressBar();

                    //Starts remote service that will handle the communication & transferring of data between device and app
                    //implementation depends on design and protocol chosen for communication
                    Intent intent = new Intent(getContext(), BluetoothClassicService.class);
                    requireActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

                    Toast.makeText(getContext(), "Successfully connected to breathalyzer", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(binding.getRoot()).navigate(R.id.action_connectionDashboard_to_navigation_dashboard);
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

    public static ConnectionDashboard newInstance(String param1, String param2) {
        ConnectionDashboard fragment = new ConnectionDashboard();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mDeviceList = new ArrayList<>();
        BluetoothManager manager = (BluetoothManager) requireActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = manager.getAdapter();
        mBluetoothAdapter.enable();

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        for(BluetoothDevice x : pairedDevices){
            Log.w("inapp - Paired Devices", x.getName()+ " "+x.getAddress());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentConnectionDashboardBinding.inflate(inflater, container, false);
        connect_button = binding.button;
        spin_progress = binding.dashboardProgressBar;

        ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
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

        connect_button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                if(!mBluetoothAdapter.isEnabled()){
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startForResult.launch(intent);
                }else{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermission();
                    }
                    startBluetoothDiscovery();
                }




//                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_connectionDashboard_to_navigation_dashboard);
            }
        });
        return binding.getRoot();
    }

    private void toggleProgressBar(){
        isScanning = !isScanning;
        if(isScanning){
            spin_progress.setVisibility(View.VISIBLE);
            connect_button.setEnabled(false);

            //This would be required if using startDiscovery since it only scan for around 12 seconds
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                //Force stop animation after timeout
                spin_progress.setVisibility(View.GONE);
                connect_button.setEnabled(true);
            }, 12000);

        }else{
            spin_progress.setVisibility(View.GONE);
            connect_button.setEnabled(true);
        }
    }

    @SuppressLint("MissingPermission")
    private void startBluetoothDiscovery(){
        toggleProgressBar();

        BluetoothDeviceFilter deviceFilter = new BluetoothDeviceFilter.Builder().setNamePattern(Pattern.compile("kdeoliveira")).build();

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
    @Override
    public void onResume() {
        super.onResume();
        if(mBluetoothAdapter == null){
            Toast.makeText(getContext(), "Bluetooth is not supported in this device", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(isScanning){
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_connectionDashboard_to_navigation_dashboard);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onStop() {
        super.onStop();
        if(mBluetoothAdapter != null){
            mBluetoothAdapter.disable();
        }
        if(bluetoothClassicService != null)
            requireActivity().unbindService(serviceConnection);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();


    }
}