package com.coen390.abreath.ui.dashboard;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.coen390.abreath.R;
import com.coen390.abreath.common.Constant;
import com.coen390.abreath.common.Utility;
import com.coen390.abreath.databinding.FragmentDashboardBinding;
import com.coen390.abreath.domain.SaveLastLevelUseCase;
import com.coen390.abreath.service.BleService;
import com.coen390.abreath.service.BluetoothServiceConnection;
import com.coen390.abreath.ui.model.DashboardViewModel;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;


import java.util.ArrayList;
import java.util.List;


public class DashboardFragment extends Fragment {

    private PieChart pieChart;
    private PieChart pieIndex;
    float userdata = 0.06f;
    float threshold = 0.08f;
    private BleService bluetoothService;
    private List<Float> chartValues;
    private DashboardViewModel dashboardViewModel;
    private BroadcastReceiver gattUpdateReceiver;
    private ServiceConnection serviceConnection;


    private void PieData() {
        if(userdata > 2*threshold)
        {
            userdata = 2*threshold;
        }
        ArrayList<PieEntry> DataIn = new ArrayList<>();
        DataIn.add(new PieEntry(userdata));
        DataIn.add(new PieEntry(0.16f - userdata));

        ArrayList<Integer> colors = new ArrayList<>();
        if(userdata >= threshold) {
            colors.add(0xffD32121);
            colors.add(80000000);
        }
        else if(userdata >= threshold-0.02f && userdata < threshold){
            colors.add(0xffFE9B24);
            colors.add(80000000);
        }
        else
        {
            colors.add(0xff387524);
            colors.add(80000000);
        }


        PieDataSet dataSet = new PieDataSet(DataIn,"");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(0f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.invalidate();
        pieChart.setHoleColor(80000000);
        pieChart.animateY(2000, Easing.EaseInOutQuad);
    }
    private void PieIndex() {

        ArrayList<PieEntry> DataIndex = new ArrayList<>();
        DataIndex.add(new PieEntry(0.06f));
        DataIndex.add(new PieEntry(0.02f));
        DataIndex.add(new PieEntry(0.08f));

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(0xFF76C498);
        colors.add(0xffFFD077);
        colors.add(0xffFA6252);




        PieDataSet dataSet = new PieDataSet(DataIndex,"");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(0f);
        data.setValueTextColor(Color.BLACK);

        pieIndex.setData(data);
        pieIndex.invalidate();
        pieIndex.setHoleColor(80000000);
        pieIndex.animateY(2000, Easing.EaseInOutQuad);
    }

    private FragmentDashboardBinding binding;
    private boolean hasRead = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chartValues = new ArrayList<>();

        new Handler().postDelayed(() -> {
            bluetoothService.setCharacteristicNotification();
        }, 2000);

        serviceConnection = new BluetoothServiceConnection(new BluetoothServiceConnection.onBleService() {
            @Override
            public void onConnected(BleService bleService) {
                bluetoothService = bleService;

                bluetoothService.getBluetoothResult().observe(getViewLifecycleOwner(), aFloat -> {
                    if(!hasRead){
                        userdata = Utility.map(aFloat, 0, 20, 0, 0.2f);
                        PieData();
                        PieIndex();
                        dashboardViewModel.setData(userdata);
                        hasRead = true;
                        new SaveLastLevelUseCase().call(userdata);
                    }

                });
            }
            @Override
            public void onDisconnected(ComponentName componentName) {
                bluetoothService = null;
            }
        });


        gattUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                if(BleService.ACTION_GATT_CONNECTED.equals(action)){
                    Log.d("TAG", "connected");
                }else if(BleService.ACTION_GATT_DISCONNECTED.equals(action)){
                    Toast.makeText(context, "Disconnected", Toast.LENGTH_LONG).show();
                }else if(BleService.ACTION_READ_DATA.equals(action)){
                    String payload = intent.getStringExtra(BleService.BLE_READ_STRING);
                    if(payload != null){
//                        if(!hasRead){
//                        }
                    }
                }
            }
        };
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDashboard;

        final TextView UserView = binding.textUsername;
        final TextView DataView = binding.resultsDisplay;


        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        dashboardViewModel.getUsername().observe(getViewLifecycleOwner(), UserView::setText);



        dashboardViewModel.getData().observe(getViewLifecycleOwner(), DataView::setText);

        if(userdata >= threshold)
        {
            textView.setTextColor(0xffD32121);
        }
        else if(userdata >= threshold-0.02f && userdata < threshold)
        {


            textView.setTextColor(0xffFE9B24);
        }
        else
        {
            textView.setTextColor(0xff387524);
        }


        pieChart = binding.piechartDisplay;
        pieIndex = binding.piechartIndex;



//        PieData();
//        PieIndex();
        pieChart.getDescription().setEnabled(false);
        pieChart.setHoleRadius(60);
        Legend none = pieChart.getLegend();
        none.setEnabled(false);

        pieIndex.getDescription().setEnabled(false);
        pieIndex.setHoleRadius(90);
        Legend none2 = pieIndex.getLegend();
        none2.setEnabled(false);

        return root;

    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(getContext(), BleService.class);
        requireActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onResume() {
        super.onResume();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BleService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BleService.ACTION_READ_DATA);
        requireContext().registerReceiver(gattUpdateReceiver, intentFilter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        if(bluetoothService != null)
            requireActivity().unbindService(serviceConnection);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        if(userdata >= threshold)
        {

        }
        else if(userdata >= threshold-0.02f && userdata < threshold)
        {

            View red_triangle = (View) getView().findViewById(R.id.red_display);
            red_triangle .setVisibility(View.INVISIBLE);
        }
        else
        {
            View red_triangle = (View) getView().findViewById(R.id.red_display);
            red_triangle .setVisibility(View.INVISIBLE);
            View orange_triangle = (View) getView().findViewById(R.id.orange_display);
            orange_triangle .setVisibility(View.INVISIBLE);
        }

    }

}