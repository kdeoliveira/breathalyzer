package com.coen390.abreath.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.coen390.abreath.R;
import com.coen390.abreath.data.api.MockUpRepository;
import com.coen390.abreath.data.api.MockUpService;
import com.coen390.abreath.data.api.MockUpServiceBuilder;
import com.coen390.abreath.data.entity.UserDataEntity;
import com.coen390.abreath.databinding.FragmentHomeBinding;
import com.coen390.abreath.ui.model.UserDataViewModel;
import com.coen390.abreath.ui.model.ViewModelFactory;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

//https://developer.android.com/guide/fragments/communicate
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private ProfileGraphFragment graph;
    private BarChart chart;
    private TextView nameTextView, ageTextView, heightTextView, weightTextView, lastnameTextView, usernameTextView;
    private ImageView profileImage;
    private String name, age, height;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        nameTextView = binding.profileName;
        lastnameTextView = binding.profileLastname;
        usernameTextView = binding.profileUsername;
        ageTextView = binding.profileAge;
        heightTextView = binding.profileHeight;
        profileImage = binding.profileImage;
        weightTextView = binding.profileWeight;



        //Note that this should be moved into onViewCreated to ensure parent activity or this view has been created before setting ViewModels
        UserDataViewModel sampleModel = new ViewModelProvider(this, new ViewModelFactory(new MockUpRepository(MockUpServiceBuilder.create(MockUpService.class)))).get(UserDataViewModel.class);

        sampleModel.getUserInfo().observe(getViewLifecycleOwner(), userDataEntity -> {
            nameTextView.setText(userDataEntity.getName());
            lastnameTextView.setText(userDataEntity.getLast_name());
            usernameTextView.setText("@".concat(userDataEntity.getUsername()));
            ageTextView.setText(String.format(Locale.CANADA,"%d", userDataEntity.getAge()));
            heightTextView.setText(String.format(Locale.CANADA,"%.2f cm", userDataEntity.getHeight()));
            weightTextView.setText(String.format(Locale.CANADA,"%d kg", userDataEntity.getWeight()));

        });

        return root;


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Fragment profileGraphFragment = new ProfileGraphFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        transaction.replace(R.id.fragmentContainerView, profileGraphFragment).commit();
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


}