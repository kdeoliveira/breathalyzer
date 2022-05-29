package com.coen390.abreath.ui.home;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coen390.abreath.R;
import com.coen390.abreath.common.Tuple;
import com.coen390.abreath.data.api.MockUpRepository;
import com.coen390.abreath.data.api.MockUpService;
import com.coen390.abreath.data.api.MockUpServiceBuilder;
import com.coen390.abreath.ui.model.UserDataViewModel;
import com.coen390.abreath.ui.model.ViewModelFactory;
import com.github.mikephil.charting.charts.BarChart;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileGraphFragment} factory method to
 * create an instance of this fragment.
 */

//https://learntodroid.com/how-to-display-a-bar-chart-in-your-android-app/#:~:text=To%20display%20a%20bar%20chart%20in%20your%20Android%20app%20you,appearance%20of%20the%20bar%20chart
public class ProfileGraphFragment extends Fragment {


    private static final int MAX_X_VALUE = 7;
    private static final int MAX_Y_VALUE = 50;
    private static final int MIN_Y_VALUE = 5;
    private static final String SET_LABEL = "App Downloads";
    private static final String[] DAYS = { "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT" };


    private BarChart chart;

    private BarData createChartData() {
        ArrayList<BarEntry> values = new ArrayList<>();
        for (int i = 0; i < MAX_X_VALUE; i++) {
            float x = i;
            // float y = new Util().randomFloatBetween(MIN_Y_VALUE, MAX_Y_VALUE);
            float y = MIN_Y_VALUE + new Random().nextFloat() * (MAX_Y_VALUE - MIN_Y_VALUE);
            values.add(new BarEntry(x, y));
        }

        BarDataSet set1 = new BarDataSet(values, SET_LABEL);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        return new BarData(dataSets);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_graph, container, false);

        chart = view.findViewById(R.id.fragment_verticalbarchart_chart);

        RoundBarRender roundBarRender = new RoundBarRender(chart, chart.getAnimator(), chart.getViewPortHandler());
//        roundBarRender.initBuffers();
        roundBarRender.setRadius(20);
        chart.setRenderer(roundBarRender);

        UserDataViewModel sampleModel = new ViewModelProvider(requireParentFragment(), new ViewModelFactory(new MockUpRepository(MockUpServiceBuilder.create(MockUpService.class)))).get(UserDataViewModel.class);

        sampleModel.getUserData().observe(getViewLifecycleOwner(), this::onChanged);


        configureChartAppearance();
//        prepareChartData(data);

        return view;
    }
    private void prepareChartData(BarData data) {


        data.setValueTextSize(12f);
        chart.setData(data);
        chart.invalidate();
    }

    private void configureChartAppearance() {
        XAxis xAxis = chart.getXAxis();



        xAxis.setDrawAxisLine(false);
        xAxis.setLabelCount(0, true);
       xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "";
            }
        });

        xAxis.setLabelCount(0, true);

//        xAxis.setTextSize(12);
//        xAxis.setAxisLineColor(R.color.primaryDarkColor);
        xAxis.setGranularity(1f);
//        xAxis.setGranularityEnabled(true);
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setXOffset(0f); //labels x offset in dps
        xAxis.setYOffset(0f); //labels y offset in dps
        xAxis.setCenterAxisLabels(false);





        YAxis yAxis = chart.getAxisRight();

        yAxis.setTextColor(R.color.primaryDarkColor);
        yAxis.setTextSize(12);
        yAxis.setAxisLineColor(R.color.primaryDarkColor);
        yAxis.setDrawGridLines(true);
        yAxis.setGranularity(1f);
        yAxis.setGranularityEnabled(true);



        yAxis.setDrawAxisLine(false);
//        yAxis.setValueFormatter(new ValueFormatter() {
//            @Override
//            public String getFormattedValue(float value) {
//                return "";
//            }
//        });

        yAxis.setLabelCount(4, true); //labels (Y-Values) for 4 horizontal grid lines
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

        YAxis leftAxis = chart.getAxisLeft();


        leftAxis.setDrawAxisLine(false);
        leftAxis.setLabelCount(0, true);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "";
            }
        });
        chart.getXAxis().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBorders(false);
        chart.setScaleEnabled(false);
        chart.setPinchZoom(false);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);

    }
    //https://getridbug.com/android/plot-data-value-on-timeline-axis-in-bar-chart-using-mpandroidchart/
    private void onChanged(Tuple<List<String>, BarData> dataTuple) {
//        chart.getXAxis().setLabelCount(dataTuple.getFirst().size() / 2, true);
//        chart.getXAxis().setValueFormatter(new ValueFormatter() {
//            @Override
//            public String getFormattedValue(float value) {
//                return dataTuple.getFirst().get((int) value);
//            }
//        });

        chart.setXAxisRenderer(new XAxisRenderer(chart.getViewPortHandler(), this.chart.getXAxis(), chart.getTransformer(YAxis.AxisDependency.LEFT)){
            @Override
            protected void drawLabel(Canvas c, String formattedLabel, float x, float y, MPPointF anchor, float angleDegrees) {
                Utils.drawXAxisValue(c, formattedLabel, x+Utils.convertDpToPixel(5f), y+Utils.convertDpToPixel(1f), mAxisLabelPaint, anchor, 90f);
            }
        });


        dataTuple.getSecond().setDrawValues(false);

        dataTuple.getSecond().setBarWidth(0.5f);

        chart.setData(dataTuple.getSecond());



        chart.animateX(1000);
        chart.animateY(1000);
        chart.invalidate();
    }
}