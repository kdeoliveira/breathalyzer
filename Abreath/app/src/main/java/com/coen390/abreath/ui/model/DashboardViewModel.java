package com.coen390.abreath.ui.model;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


/**
 * Holds state used by the Pie Chart which displays the test results received from the sensor
 */
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