package com.coen390.abreath.ui.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DashboardViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<String> Username;
    private final MutableLiveData<String> DisplayData;
    String username = "xxx";

    float userdata = 0.08f;
    float threshold = 0.08f;

    public DashboardViewModel() {
        mText = new MutableLiveData<>();
        Username = new MutableLiveData<>();
        DisplayData= new MutableLiveData<>();

        DisplayData.setValue(userdata + "mg/L");
        Username.setValue(username + ", the results are out :");

        if(userdata => threshold)
        {
            mText.setValue(username + ", you are above the legal limit! \n Please do not take the wheel.");
        }
        else if(userdata < threshold)
        {
            mText.setValue(username + ", you are under the legal limit. \n You are good to drive! ");
        }
        else
        {
            mText.setValue("Test Unsuccessful.\n Please try again.");
        }

    }





    public LiveData<String> getText() {
        return mText;
    }
    public LiveData<String> getUsername() {
        return Username;
    }
    public LiveData<String> getData() {
        return DisplayData;
    }
    public float Data() {
        return userdata;
    }



}