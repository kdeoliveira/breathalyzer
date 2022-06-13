package com.coen390.abreath.ui.model;

import static android.app.PendingIntent.getActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.coen390.abreath.R;

import java.util.Locale;

public class DashboardViewModel extends ViewModel {

    private final MutableLiveData<Float> mDisplayData;

    public DashboardViewModel() {
        mDisplayData = new MutableLiveData<>();
//        mDisplayData.setValue(0.0f);
    }

    public LiveData<Float> getData() {
        return mDisplayData;
    }


    public void setData(Float input){
        mDisplayData.setValue(input);
    }




}