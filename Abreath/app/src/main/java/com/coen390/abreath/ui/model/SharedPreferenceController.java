package com.coen390.abreath.ui.model;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceController {
    private final SharedPreferences units;
    private final SharedPreferences night_mode;
    public SharedPreferenceController(Context context){
        units = context.getSharedPreferences("units", Context.MODE_PRIVATE);
        night_mode = context.getSharedPreferences("night",Context.MODE_PRIVATE);
    }

    public boolean getWeight(){
        return units.getBoolean("weight", true);
    }

    public boolean getHeight(){
        return units.getBoolean("height", true);
    }


    public boolean getNightMode(){return night_mode.getBoolean("night_mode", false); }

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
