package com.coen390.abreath.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class SampleData extends ViewModel {
    private MutableLiveData<SampleEntity> samples;

    public SampleData() {
        samples = new MutableLiveData<>();
    }

    public LiveData<SampleEntity> getSamples(){
        if(samples != null){
            return this.samples;
        }
        samples = new MutableLiveData<>();
        return samples;
    }



}
