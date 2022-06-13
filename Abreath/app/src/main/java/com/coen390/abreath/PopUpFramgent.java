package com.coen390.abreath;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.coen390.abreath.databinding.FragmentPopUpFramgentBinding;
import com.google.android.material.tabs.TabLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PopUpFramgent#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PopUpFramgent extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PopUpFramgent() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PopUpFramgent.
     */
    // TODO: Rename and change types and number of parameters
    public static PopUpFramgent newInstance(String param1, String param2) {
        PopUpFramgent fragment = new PopUpFramgent();
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
        // Inflate the layout for this fragment

        binding = FragmentPopUpFramgentBinding.inflate(inflater, container, false);

        TextView help = binding.popupText;
        if (mParam1 == "home") {
            help.setText("Welcome to the Home Page!\n\nABreath is an application that has for goal to reduce the number of drunk drivers on the road.\n\nThis is where you will find your profile page and your personalized data chart that displays your test history over time.\n\nIf you have recently done a test and were above the limit, a counter will give you an estimation on how long it will take for you to go under the limit driving limit.\n\nIf you wish to see if you are legally eligible to take the wheel, tap the main button!\n\n");
        } else if (mParam1 == "bluetooth") {
            help.setText("Welcome to the Bluetooth Connection Page!\n\nThis page is where you will link you breathalyzer to the application via bluetooth.\n\nMake sure the bluetooth on your phone is activated and tap the Start button.\n\nOnce you are connected, blow into the breathalyzer and wait a few seconds while we prepare your results!\n\n Once the test has been completed, you will redirected to the results page which will indicate you if you are legally eligible to drive or not.\n\nIt does NOT get easier than that!\n\n");

        } else if (mParam1 == "dashboard") {
            help.setText("Welcome to the Dashboard page!\n\nAbreath came up with a simple color code:\ngreen, orange and red.\n\nDepending on the color that appears on your Dashboard page, a guidance text will follow and will let you know if you are eligible to legally drive or not.\n\nIf over the limit, a countdown will appear indicating the estimated time it will take for your BAC levels to go back under the threshold.\n\n Once that timer hits 0, take another test to confirm.\n");

        } else if (mParam1 == "settings") {
            help.setText("Welcome to the Settings page!\n\n This is where you can change your profile settings and preferences.\n\n The Account page will allow you to change your identity card.\n\nThe Appearance page will allow you switch the app in night mode.\n\n The Units page will let change the units from metric to imperial.\n\n The Help and About page will give you more context and information about the Abreath application.\n\n");

        }

        Button esc = binding.escButton;

        esc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return binding.getRoot();

    }
    private FragmentPopUpFramgentBinding binding;
    public final static String TAG = "coen390.popup";
}