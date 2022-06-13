package com.coen390.abreath.ui.dashboard;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextSwitcher;

import com.coen390.abreath.databinding.FragmentLoadingBinding;
import com.github.ybq.android.spinkit.SpinKitView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoadingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoadingFragment extends DialogFragment {
    public final static String TAG = "coen390.dashboard.loading";

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private TextSwitcher textSwitcher;
    private String mCurrentState;

    private FragmentLoadingBinding fragmentLoadingBinding;

    public LoadingFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment LoadingFragment.
     */
    public static LoadingFragment newInstance(String param1) {
        LoadingFragment fragment = new LoadingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCurrentState = getArguments().getString(ARG_PARAM1);
        }else{
            mCurrentState = "";
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentLoadingBinding = FragmentLoadingBinding.inflate(inflater, container, false);
        View view = fragmentLoadingBinding.getRoot();
        setCancelable(false);
        fragmentLoadingBinding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Fragment parentFragment = getParentFragment();
                if(parentFragment instanceof Dissmissable)
                    ((Dissmissable) parentFragment).onDismissAction();
            }
        });
        return view;
    }

    public SpinKitView getSpinKit(){

        return null;
    }

    public void setNotFound(String title){
        if(fragmentLoadingBinding == null) return;
        textSwitcher.setText(title);
        fragmentLoadingBinding.loadingProgressBar.setVisibility(View.GONE);
        fragmentLoadingBinding.button2.setVisibility(View.VISIBLE);
        setCancelable(true);

    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textSwitcher = fragmentLoadingBinding.loadingState;

        textSwitcher.setInAnimation(getContext(), android.R.anim.slide_in_left);
        textSwitcher.setOutAnimation(getContext(), android.R.anim.slide_out_right);
        textSwitcher.setText(mCurrentState);
    }

    public void setStateText(String text){
        textSwitcher.setText(text);
    }

    public interface Dissmissable {
        void onDismissAction();
    }
}