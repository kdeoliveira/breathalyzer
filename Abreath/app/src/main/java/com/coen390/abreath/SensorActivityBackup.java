//package com.coen390.abreath;
//
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.NonNull;
//import androidx.annotation.RequiresApi;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothGatt;
//import android.bluetooth.BluetoothManager;
//import android.bluetooth.le.BluetoothLeScanner;
//import android.bluetooth.le.ScanCallback;
//import android.bluetooth.le.ScanFilter;
//import android.bluetooth.le.ScanResult;
//import android.bluetooth.le.ScanSettings;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.util.Log;
//import android.util.SparseArray;
//import android.view.View;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.ListView;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//public class SensorActivity extends AppCompatActivity {
//    private TextView sensorReading, sensorStatus;
//    private Button scan;
//    private ProgressBar progressBar;
//    private ListView listView;
//
//    private static final String TAG = "com.coen390.abreath.BLUETOOTH";
//    private static final int REQUEST_CODE = 71567;
//    private static final String DEVICE_NAME = "com.coen390.abreath.SENSOR";
//    private BluetoothAdapter mBluetoothAdapter;
//    private BluetoothLeScanner mBluetoothScanner;
//    private Set<BluetoothDevice> mDevices;
//
//    private List<String> mDeviceNames;
//    private BluetoothGatt mConnectedGatt;
//    private boolean isLocationPermissionRequired = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
//
//
//    private boolean isScanning;
//    private Handler mHandler;
//
//
//    private ScanCallback scanCallback = new ScanCallback() {
//        @Override
//        public void onScanResult(int callbackType, ScanResult result) {
//            super.onScanResult(callbackType, result);
//
//            Toast.makeText(SensorActivity.this, "Founded: "+result.toString(), Toast.LENGTH_SHORT).show();
//
//            if (result.getDevice() == null) return;
//
//            mDevices.add(result.getDevice());
//            mDeviceNames.add(result.getDevice().getAddress());
//
//
//        }
//
//        @Override
//        public void onBatchScanResults(List<ScanResult> results) {
//            super.onBatchScanResults(results);
//        }
//
//        @Override
//        public void onScanFailed(int errorCode) {
//            Log.d("inapp", String.valueOf(errorCode));
//            super.onScanFailed(errorCode);
//        }
//    };
//
//    @RequiresApi(api = Build.VERSION_CODES.N)
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_sensor);
//
//        setProgressBarIndeterminate(true);
//
//        mHandler = new Handler();
//
//        mDevices = new HashSet<>();
//        mDeviceNames = new ArrayList<>();
//        sensorReading = findViewById(R.id.sensorReading);
//        sensorStatus = findViewById(R.id.statusBluetooth);
//        scan = findViewById(R.id.sensorScan);
//        listView = findViewById(R.id.listOfBle);
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mDeviceNames) ;
//        listView.setAdapter(adapter);
//
//
//        progressBar = findViewById(R.id.progressBar);
//
//        BluetoothManager manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
//        mBluetoothAdapter = manager.getAdapter();
//        mBluetoothScanner = manager.getAdapter().getBluetoothLeScanner();
//
//
//        progressBar.setIndeterminate(true);
//
//
//        scan.setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.M)
//            @Override
//            public void onClick(View view) {
//                mDevices.clear();
//                if (Build.VERSION.SDK_INT >= 23) {
//                    if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(SensorActivity.this);
//                        builder.setTitle("This app needs location access");
//                        builder.setMessage("Please grant location access so this app can detect peripherals.");
//                        builder.setPositiveButton(android.R.string.ok, null);
//                        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                            @Override
//                            public void onDismiss(DialogInterface dialogInterface) {
//                                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
//                            }
//                        });
//                        builder.show();
//                    }
//                }
//                if(!isScanning){
//                    mHandler.postDelayed(new Runnable() {
//                        @SuppressLint("MissingPermission")
//                        @Override
//                        public void run() {
//                            isScanning = false;
//                            mBluetoothScanner.stopScan(scanCallback);
//                            listView.invalidateViews();
//                            progressBar.setVisibility(View.GONE);
//                        }
//                    }, 1000);
//                    isScanning = true;
//                    progressBar.setVisibility(View.VISIBLE);
//                    mBluetoothScanner.startScan(getScanFilters(), getScanSettings(), scanCallback);
//                }else{
//                    isScanning = false;
//                    mBluetoothScanner.stopScan(scanCallback);
//                    listView.invalidateViews();
//                    progressBar.setVisibility(View.GONE);
//
//                }
//
//            }
//        });
//
//
//    }
//
//
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_CODE) {
//            if (grantResults.length > 0
//                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(SensorActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(SensorActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
//            }
//        }
//
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//    }
//
//
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//
//        //https://developer.android.com/training/permissions/requesting#java
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
//            Log.d("Permission inapp", "Bluetooth Connected");
//            return;
//        } else if (shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH_CONNECT)) {
//            Log.d("Permission inapp", "explain to the user why your app requires this\n" +
//                    "    // permission for a specific feature to behave as expected");
//        } else {
//            Log.d("Permission inapp", "We need permissions");
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//
//                requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_CODE);
//
//            }
//        }
//
//
//        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
//            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivity(enableIntent);
//
//            finish();
//            return;
//        }
//
//        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//            Toast.makeText(this, "No LE Support for this device", Toast.LENGTH_LONG).show();
//            finish();
//        }
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (mConnectedGatt != null) {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }
//            mConnectedGatt.disconnect();
//            mConnectedGatt = null;
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        mBluetoothScanner.stopScan(scanCallback);
//
//    }
//    //https://stackoverflow.com/questions/68036622/android-ble-scan-callback-is-not-called-after-scanning-for-ble-devices
//    private static List<ScanFilter> getScanFilters(){
//        List<ScanFilter> scanFilters = new ArrayList<>();
//        ScanFilter.Builder builder = new ScanFilter.Builder();
////        builder.setServiceUuid(UUID)
//        builder.setDeviceName("Kev");
//        scanFilters.add(builder.build());
//
//        return scanFilters;
//    }
//
//    private static ScanSettings getScanSettings(){
//        ScanSettings.Builder builder = new ScanSettings.Builder();
//        builder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);
//        return builder.build();
//    }
//
//}