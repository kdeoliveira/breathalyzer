package com.coen390.abreath.ui.home;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.coen390.abreath.R;
import com.coen390.abreath.data.api.MockUpRepository;
import com.coen390.abreath.data.api.MockUpService;
import com.coen390.abreath.data.api.MockUpServiceBuilder;
import com.coen390.abreath.databinding.FragmentHomeBinding;
import com.coen390.abreath.common.Tuple;
import com.coen390.abreath.ui.model.UserDataViewModel;
import com.coen390.abreath.ui.model.ViewModelFactory;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
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


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private ProfileGraph graph;
    public TextView nameTextView, ageTextView, heightTextView, weightTextView;
    private ImageView profileImage;
    private String name, age, height;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        nameTextView = binding.profileName;
        ageTextView = binding.profileAge;
        heightTextView = binding.profileHeight;
        profileImage = binding.profileImage;


        UserDataViewModel sampleModel = new ViewModelProvider(this, new ViewModelFactory(new MockUpRepository(MockUpServiceBuilder.create(MockUpService.class)))).get(UserDataViewModel.class);


        chart = binding.fragmentVerticalbarchartChart;



        sampleModel.getUserData().observe(getViewLifecycleOwner(), this::onChanged);

        chart.setNoDataText("Loading");
        Paint p = chart.getPaint(Chart.PAINT_INFO);
        p.setTextSize(12f);



        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;


    }

    private BarData createChartData() {
        ArrayList<BarEntry> values = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            float x = i;

            float y = 5 + new Random().nextFloat() * (50 - 5);
            values.add(new BarEntry(x, y));
        }

        BarDataSet set1 = new BarDataSet(values, "Tests");

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);

        return data;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    //https://getridbug.com/android/plot-data-value-on-timeline-axis-in-bar-chart-using-mpandroidchart/
    private void onChanged(Tuple<List<String>, BarData> dataTuple) {
        XAxis xAxis = chart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setTextColor(R.color.primaryTextColor);
        xAxis.setTextSize(14);
        xAxis.setDrawAxisLine(true);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setLabelCount(dataTuple.getFirst().size() / 4, true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setXOffset(0f); //labels x offset in dps
        xAxis.setYOffset(0f); //labels y offset in dps
        xAxis.setCenterAxisLabels(true);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return dataTuple.getFirst().get((int) value);
            }
        });


        YAxis yAxis = chart.getAxisRight();
        yAxis.setTextColor(R.color.primaryDarkColor);
        yAxis.setTextSize(14);
        yAxis.setDrawAxisLine(true);
        yAxis.setAxisLineColor(R.color.primaryDarkColor);
        yAxis.setDrawGridLines(true);
        yAxis.setGranularity(1f);
        yAxis.setGranularityEnabled(true);
        yAxis.setAxisMinimum(0);
        yAxis.setAxisMaximum(1500f);
        yAxis.setLabelCount(4, true); //labels (Y-Values) for 4 horizontal grid lines
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinimum(0);
        leftAxis.setDrawAxisLine(true);
        leftAxis.setLabelCount(0, true);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "";
            }
        });

        chart.setXAxisRenderer(new XAxisRenderer(chart.getViewPortHandler(), xAxis, chart.getTransformer(YAxis.AxisDependency.LEFT)){
            @Override
            protected void drawLabel(Canvas c, String formattedLabel, float x, float y, MPPointF anchor, float angleDegrees) {
                Utils.drawXAxisValue(c, formattedLabel, x+Utils.convertDpToPixel(5f), y+Utils.convertDpToPixel(1f), mAxisLabelPaint, anchor, 90f);
            }
        });
        chart.setData(dataTuple.getSecond());
        chart.setScaleEnabled(true);
        chart.setPinchZoom(false);
        chart.invalidate();
    }
}