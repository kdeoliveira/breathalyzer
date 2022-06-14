package com.coen390.abreath.ui.dashboard;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.coen390.abreath.ui.home.HomeFragment;
import com.coen390.abreath.ui.model.DashboardViewModel;
import com.coen390.abreath.ui.model.SharedPreferenceController;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;


import java.time.Duration;
import java.util.ArrayList;
import java.util.Locale;


public class DashboardFragment extends Fragment implements LoadingFragment.Dissmissable {

    private PieChart pieChart;
    private PieChart pieIndex;
    private TextView textView;
    private float userdata;
    private final static float THRESHOLD = 0.08f;
    private BleService bluetoothService;
    private DashboardViewModel dashboardViewModel;
    private BroadcastReceiver gattUpdateReceiver;
    private ServiceConnection serviceConnection;
    private Handler handlerAwaiting, handlerNotFound;
    private SharedPreferenceController sp;



    private void PieData() {
        if(userdata > 2* THRESHOLD)
        {
            userdata = 2* THRESHOLD;
        }
        ArrayList<PieEntry> DataIn = new ArrayList<>();
        DataIn.add(new PieEntry(userdata));
        DataIn.add(new PieEntry(0.16f - userdata));

        ArrayList<Integer> colors = new ArrayList<>();
        if(userdata >= THRESHOLD) {
            colors.add(0xffD32121);
            colors.add(80000000);

            textView.setTextColor(0xffD32121);
        }
        else if(userdata >= THRESHOLD -0.02f && userdata < THRESHOLD){
            colors.add(0xffFE9B24);
            colors.add(80000000);
            textView.setTextColor(0xffFE9B24);
            View red_triangle = (View) getView().findViewById(R.id.red_display);
            red_triangle.setVisibility(View.INVISIBLE);
        }
        else
        {
            colors.add(0xff387524);
            colors.add(80000000);
            View red_triangle = (View) getView().findViewById(R.id.red_display);
            red_triangle .setVisibility(View.INVISIBLE);
            View orange_triangle = (View) getView().findViewById(R.id.orange_display);
            orange_triangle .setVisibility(View.INVISIBLE);
            textView.setTextColor(0xff387524);

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
        }, 20000);


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
                            sp.setUserData(userdata);
                        }, 500);
                        handlerNotFound.removeCallbacksAndMessages(null);
                    }
                });
                bluetoothService.getBluetoothResult().observe(getViewLifecycleOwner(), floatList -> {
//                    double sensor_volt = floatList.stream().mapToDouble(x -> x).average().getAsDouble();
                    if(floatList.size() == 0) {
                        dashboardViewModel.setData(0.0f);
                        return;
                    }

                    float sensor_volt = floatList.get(floatList.size() - 1);
                    Log.d("DashboardFragment", String.valueOf(sensor_volt));
                    //                        userdata = Utility.map(floatList, 0, 20, 0, 0.16f);
                        userdata = (float) sensor_volt*0.01f; //TODO incorrect value provided by the sensor

                        dashboardViewModel.setData(userdata);
                    Log.d("ViewModel", "dashbaordViewModel");

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

        binding = FragmentDashboardBinding.inflate(inflater, container, false);

        dashboardViewModel =
                new ViewModelProvider(requireActivity()).get(DashboardViewModel.class);

        View root = binding.getRoot();
        sp = new SharedPreferenceController(root.getContext());

        root.setVisibility(View.GONE);
        textView = binding.textDashboard;
        final TextView DataView = binding.resultsDisplay;




        dashboardViewModel.getData().observe(getViewLifecycleOwner(), aFloat -> {
            DataView.setText(String.format(Locale.CANADA, "BAC %.3f %%",aFloat));
            if(aFloat >= THRESHOLD)
                textView.setText("You are above the legal limit! \nPlease do not take the wheel.");
            else if(aFloat >= THRESHOLD - .02f && aFloat < THRESHOLD)
                textView.setText("You are not above the legal limit. \n But it is recommended you do not drive.");
            else
                textView.setText(("You are under the legal limit.\nYou are good to drive! "));
        });

        pieChart = binding.piechartDisplay;
        pieChart.setNoDataText("");
        pieIndex = binding.piechartIndex;
        pieIndex.setNoDataText("");


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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Intent intent = new Intent(getActivity(), HomeFragment.class); //https://stackoverflow.com/questions/21953839/how-to-decide-which-activity-we-came-from
        intent.putExtra("comesFrom", "Dashboard");
    }


    @Override
    public void onDismissAction() {
        Navigation.findNavController(requireView()).navigate(R.id.to_navigation_dashboard);

    }
}