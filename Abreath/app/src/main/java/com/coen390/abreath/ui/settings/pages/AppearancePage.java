package com.coen390.abreath.ui.settings.pages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.coen390.abreath.R;

public class AppearancePage extends AppCompatActivity {

    protected TextView appearance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appearance_page);

        Switch nightmode_switch = findViewById(R.id.nightmode_switch);

        SharedPreferences night = getSharedPreferences("night",0);
        Boolean booleanValue = night.getBoolean("night_mode",true);
        if (booleanValue){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            nightmode_switch.setChecked(true);
        }

        nightmode_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    nightmode_switch.setChecked(true);
                    SharedPreferences.Editor editor = night.edit();
                    editor.putBoolean("night_mode",true);
                    editor.commit();


                }else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    nightmode_switch.setChecked(false);
                    SharedPreferences.Editor editor = night.edit();
                    editor.putBoolean("night_mode",false);
                    editor.commit();
                    String height = "178";
                    String weight ="100";
                    SharedPreferences sp;
                    sp = getSharedPreferences("units" , Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor2= sp.edit();
                    editor2.putString("user_height", height);
                    editor2.putString("height", height + "cm");
                    editor2.putString("user_weight", weight);
                    editor2.putString("weight", weight + "kg");
                    editor2.commit();

                }
            }
        });
    }

}

