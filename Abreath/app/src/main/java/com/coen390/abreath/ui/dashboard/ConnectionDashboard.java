package com.coen390.abreath.ui.dashboard;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.coen390.abreath.R;
import com.coen390.abreath.databinding.FragmentConnectionDashboardBinding;


public class ConnectionDashboard extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private FragmentConnectionDashboardBinding binding;
    private Button connect_button;
    private Boolean is_bluetooth_connected = true;


    public ConnectionDashboard() {}

    public static ConnectionDashboard newInstance(String param1, String param2) {
        ConnectionDashboard fragment = new ConnectionDashboard();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentConnectionDashboardBinding.inflate(inflater, container, false);
        connect_button = binding.button;
        connect_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_connectionDashboard_to_navigation_dashboard);
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(is_bluetooth_connected){
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_connectionDashboard_to_navigation_dashboard);
        }
    }
}