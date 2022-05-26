package com.coen390.abreath.model;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.coen390.abreath.api.MockUpController;

import java.util.concurrent.atomic.AtomicReference;

public class SampleViewModel extends ViewModel {
    private MutableLiveData<SampleEntity> samples;
    private MockUpController mockUpController;

    public SampleViewModel() {
        mockUpController = new MockUpController("https://628ea476dc478523653294a8.mockapi.io/");
        samples = new MutableLiveData<>();
        mockUpController.getUsers(data -> {
            //Async call. Check https://developer.android.com/topic/libraries/architecture/livedata#java for for specific implementation
            samples.postValue(data.get(0));
        });
    }


    public LiveData<SampleEntity> getSamples(){
        if(samples != null){
            return this.samples;
        }
        samples = new MutableLiveData<>();
        return samples;
    }



}
