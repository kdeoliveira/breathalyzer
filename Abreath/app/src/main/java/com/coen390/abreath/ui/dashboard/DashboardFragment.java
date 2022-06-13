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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.coen390.abreath.R;
import com.coen390.abreath.databinding.FragmentDashboardBinding;
import com.coen390.abreath.domain.SaveLastLevelUseCase;
import com.coen390.abreath.service.BleService;
import com.coen390.abreath.service.BluetoothServiceConnection;
import com.coen390.abreath.service.GattBroadcastReceiver;
import com.coen390.abreath.ui.model.DashboardViewModel;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.util.ArrayList;
import java.util.List;


public class DashboardFragment extends Fragment implements LoadingFragment.Dissmissable {

    private PieChart pieChart;
    private PieChart pieIndex;
    float userdata = 0.06f;
    float threshold = 0.08f;
    private BleService bluetoothService;
    private DashboardViewModel dashboardViewModel;
    private BroadcastReceiver gattUpdateReceiver;
    private ServiceConnection serviceConnection;
    private Handler handlerAwaiting, handlerNotFound;


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
    private LoadingFragment loadingFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handlerNotFound = new Handler();
        handlerAwaiting = new Handler();
        loadingFragment = LoadingFragment.newInstance("Awaiting Results");
        loadingFragment.show(getChildFragmentManager(), LoadingFragment.TAG);


        SharedPreferences frag = getActivity().getSharedPreferences("whichfrag", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = frag.edit();
        editor.putString("fragment", "dashboard");
        editor.apply();

        handlerNotFound.postDelayed(() -> {
            loadingFragment.setNotFound("Unable to fetch data");
        }, 30000);


        serviceConnection = new BluetoothServiceConnection(new BluetoothServiceConnection.onBleService() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onConnected(BleService bleService) {
                bluetoothService = bleService;
                bluetoothService.setCharacteristicNotification();
                bluetoothService.getBluetoothFinished().observe(getViewLifecycleOwner(), aBoolean -> {
                    if(aBoolean){
                        handlerAwaiting.postDelayed(() -> {
                            binding.getRoot().setVisibility(View.VISIBLE);
                            loadingFragment.dismiss();
                            PieData();
                            PieIndex();
                            new SaveLastLevelUseCase().call(userdata);
                        }, 500);
                        handlerNotFound.removeCallbacksAndMessages(null);
                    }
                });
                bluetoothService.getBluetoothResult().observe(getViewLifecycleOwner(), floatList -> {
//                    double sensor_volt = floatList.stream().mapToDouble(x -> x).average().getAsDouble();
                    float sensor_volt = floatList.get(floatList.size() - 1);
                    Log.d("DashboardFragment", String.valueOf(sensor_volt));
                    //                        userdata = Utility.map(floatList, 0, 20, 0, 0.16f);
                        userdata = (float) sensor_volt*0.0001f; //TODO incorrect value provided by the sensor

                        dashboardViewModel.setData(userdata);
                });

            }
            @Override
            public void onDisconnected(ComponentName componentName) {
                bluetoothService = null;
            }
        });


        gattUpdateReceiver = new GattBroadcastReceiver(new GattBroadcastReceiver.GattBroadcastReceiverListener() {

            @Override
            public void onActionConnected(Context context) {
                Log.d("DashboardFragment", "status connected");
            }

            @Override
            public void onActionDisconnected(Context context) {
                Toast.makeText(context, "Disconnected", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onActionReadData(Context context, String payload) {
                if(!hasRead){
                    loadingFragment.setStateText("Calculating BAC");
                    hasRead = true;
                }
            }
        });
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        root.setVisibility(View.GONE);

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
        pieChart.setNoDataText("");
        pieIndex = binding.piechartIndex;
        pieIndex.setNoDataText("");



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
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
        if(bluetoothService != null){
            bluetoothService.close();
            requireActivity().unbindService(serviceConnection);
        }
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

    @Override
    public void onDismissAction() {
        Navigation.findNavController(requireView()).navigate(R.id.to_navigation_dashboard);

    }
}