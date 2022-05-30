package com.coen390.abreath.ui.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.coen390.abreath.R;
import com.coen390.abreath.databinding.FragmentDashboardBinding;
import com.coen390.abreath.ui.model.DashboardViewModel;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;


import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;


public class DashboardFragment extends Fragment {

    private PieChart pieChart;
    float userdata = 0.08f;
    float threshold = 0.08f;



    private void PieData() {
        ArrayList<PieEntry> DataIn = new ArrayList<>();
        DataIn.add(new PieEntry(userdata+0.05f));
        DataIn.add(new PieEntry(0.25f - userdata));

        ArrayList<Integer> colors = new ArrayList<>();
        if(userdata >= threshold) {
            colors.add(0xFFAD1234);
            colors.add(0xFFCCCCCC);
        }
        else
        {
            colors.add(0xFF76C498);
            colors.add(0xFFCCCCCC);
        }



        PieDataSet dataSet = new PieDataSet(DataIn,"");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(0f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.invalidate();

        pieChart.animateY(2000, Easing.EaseInOutQuad);
    }

    private FragmentDashboardBinding binding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {




        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDashboard;

        final TextView UserView = binding.textUsername;
        final TextView DataView = binding.resultsDisplay;


        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        dashboardViewModel.getUsername().observe(getViewLifecycleOwner(), UserView::setText);
        dashboardViewModel.getData().observe(getViewLifecycleOwner(), DataView::setText);
        dashboardViewModel.Data();

        UserView.setTextColor(Color.BLACK);
        DataView.setTextColor(Color.BLACK);

        if(userdata >= threshold)
        {
            textView.setTextColor(0xFFAD1234);
        }
        else if(userdata < threshold)
        {
            textView.setTextColor(0xFF76C498);
        }
        else
        {
            textView.setTextColor(Color.BLACK);
        }

        pieChart = binding.piechartDisplay;

        PieData();

        pieChart.getDescription().setEnabled(false);
        pieChart.setHoleRadius(60);
        Legend none = pieChart.getLegend();
        none.setEnabled(false);

        return root;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }




}