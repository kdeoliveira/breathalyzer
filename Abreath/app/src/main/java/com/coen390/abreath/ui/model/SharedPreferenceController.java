package com.coen390.abreath.ui.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import java.time.Instant;
import java.util.Date;

public class SharedPreferenceController {
    private final SharedPreferences units;
    private final SharedPreferences night_mode;
    private final SharedPreferences user_data;


    public SharedPreferenceController(Context context){
        units = context.getSharedPreferences("units", Context.MODE_PRIVATE);
        night_mode = context.getSharedPreferences("night",Context.MODE_PRIVATE);
        user_data = context.getSharedPreferences("UserData", Context.MODE_PRIVATE);
    }

    public boolean getWeight(){
        return units.getBoolean("weight", true);
    }

    public boolean getHeight(){
        return units.getBoolean("height", true);
    }

    public float getUserData(){return this.user_data.getFloat("value", 0.0f);}

    public boolean getNightMode(){return night_mode.getBoolean("night_mode", false); }

    public long getUserDateTime(){return user_data.getLong("time", 0);}

    public void setUserData(float input){
        SharedPreferences.Editor editor = user_data.edit();
        editor.putFloat("value", input);
        editor.apply();
    }
    private void setUserDateTime(long time){
        SharedPreferences.Editor editor = user_data.edit();
        editor.putLong("time", time);
        editor.apply();
    }

    public void setNightMode(boolean value){
        SharedPreferences.Editor editor = night_mode.edit();
        editor.putBoolean("night_mode", value);
        editor.apply();
    }

    public void setWeight(boolean value){
        SharedPreferences.Editor editor = units.edit();
        editor.putBoolean("weight", value);
        editor.apply();
    }

    public void setHeight(boolean value){
        SharedPreferences.Editor editor = units.edit();
        editor.putBoolean("height", value);
        editor.apply();
    }

}
