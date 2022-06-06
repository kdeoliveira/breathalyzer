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

public class DashboardViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<String> Username;
    private final MutableLiveData<String> DisplayData;
    String username = "John";
    float threshold = 0.08f;
    float userdata =0.05f;

    public DashboardViewModel() {


        mText = new MutableLiveData<>();
        Username = new MutableLiveData<>();
        DisplayData= new MutableLiveData<>();

        DisplayData.setValue(userdata + "% BAC");
        Username.setValue(username + ", the results are out :");

        if(userdata >= threshold)
        {
            mText.setValue("You are above the legal limit! \nPlease do not take the wheel.");
        }
        else if(userdata >= threshold-0.02f && userdata < threshold) {
            mText.setValue("       You are not above the legal limit. \n But it is recommended you do not drive.");
        }
        else
        {
            mText.setValue("You are under the legal limit. \n     You are good to drive! ");
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




}