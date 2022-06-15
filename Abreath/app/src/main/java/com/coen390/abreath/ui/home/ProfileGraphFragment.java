package com.coen390.abreath.ui.home;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coen390.abreath.R;
import com.coen390.abreath.common.Tuple;
import com.coen390.abreath.common.Utility;
import com.coen390.abreath.data.api.MockUpRepository;
import com.coen390.abreath.data.api.MockUpService;
import com.coen390.abreath.data.api.MockUpServiceBuilder;
import com.coen390.abreath.data.entity.TestResultEntity;
import com.coen390.abreath.ui.model.UserDataViewModel;
import com.coen390.abreath.ui.model.ViewModelFactory;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileGraphFragment} factory method to
 * create an instance of this fragment.
 */

//https://learntodroid.com/how-to-display-a-bar-chart-in-your-android-app/#:~:text=To%20display%20a%20bar%20chart%20in%20your%20Android%20app%20you,appearance%20of%20the%20bar%20chart

/**
 * Fragment used for displaying the Horizontal graphs
 */
public class ProfileGraphFragment extends Fragment {

    private TextView chart_no_data;
    private BarChart chart;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_graph, container, false);

        chart = view.findViewById(R.id.fragment_verticalbarchart_chart);
        chart_no_data = view.findViewById(R.id.chart_no_data);
        RoundBarRender roundBarRender = new RoundBarRender(chart, chart.getAnimator(), chart.getViewPortHandler());

        roundBarRender.setThreashold(0.16f);
        chart.setRenderer(roundBarRender);
        chart.setNoDataText("");

        //Note that this should be moved into onViewCreated to ensure parent activity or this view has been created before setting ViewModels
        UserDataViewModel sampleModel = new ViewModelProvider(requireParentFragment()).get(UserDataViewModel.class);

        sampleModel.getUserData().observe(getViewLifecycleOwner(), this::onChanged);

        configureChartAppearance();
        return view;
    }


    /*
    Sets layout of graph based on the parameters provided by the MPAndroidChart Library
     */
    private void configureChartAppearance() {
        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawAxisLine(false);
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis yAxis = chart.getAxisRight();
        yAxis.setTextColor(R.color.primaryDarkColor);
        yAxis.setTextSize(12);
        yAxis.setAxisLineColor(R.color.primaryDarkColor);
        yAxis.setDrawGridLines(true);
        yAxis.setAxisMinimum(0.0f);
        yAxis.setAxisMaximum(0.16f);
        yAxis.setDrawAxisLine(false);
        yAxis.setLabelCount(4, true); //labels (Y-Values) for 4 horizontal grid lines
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawAxisLine(false);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(0.16f);
        leftAxis.setLabelCount(0, true);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "";
            }
        });

        chart.setDrawGridBackground(false);
        chart.setDrawBorders(false);
        chart.setScaleEnabled(false);
        chart.setPinchZoom(false);

        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);

    }
    //https://getridbug.com/android/plot-data-value-on-timeline-axis-in-bar-chart-using-mpandroidchart/

    /**
     * Sets graph data based on test results of user
     */
    private void onChanged(List<TestResultEntity> dataList) {

        chart.getXAxis().setLabelCount(dataList.size() + 1, true);
        /*
        Checks if user has previous test results in the database
         */
        if(dataList.size() > 0){
            chart.getXAxis().setValueFormatter(new GraphFormatter(dataList));
            chart_no_data.setVisibility(View.GONE);
        }
        else{
            chart_no_data.setVisibility(View.VISIBLE);
        }


        List<BarEntry> barEntries = new ArrayList<>();
        for(int i = 0; i < dataList.size() ; i++){
            barEntries.add(new BarEntry(i, dataList.get(i).getTestResult()));
        }

        BarDataSet dataSet = new BarDataSet(barEntries, "Previous Samples");
        BarData barData = new BarData(dataSet);

        barData.setDrawValues(false);
        /*
        Sets width of HBar according to the amount of data present
        This prevents the rendering of large horizontal bars when few data is displayed in the graph
         */
        barData.setBarWidth(Utility.map(dataList.size(), 1f, 12f, 0.3f, 0.5f));

        chart.setData(barData);

        chart.animateX(1000);
        chart.animateY(1000);
        chart.invalidate();
    }
}