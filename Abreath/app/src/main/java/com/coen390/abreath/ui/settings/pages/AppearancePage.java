package com.coen390.abreath.ui.settings.pages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.coen390.abreath.R;
import com.coen390.abreath.ui.model.SharedPreferenceController;

import java.util.Objects;

/**
 * Sets or unset night mode
 */
public class AppearancePage extends AppCompatActivity {

    private SharedPreferenceController sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appearance_page);

        SwitchCompat nightmode_switch = findViewById(R.id.nightmode_switch);

        sp = new SharedPreferenceController(this);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0f);



        nightmode_switch.setChecked(sp.getNightMode());

        //Help to set to Dark Mode : https://stackoverflow.com/questions/70804416/system-dark-mode-setting-problem-more-dark-mode-options
        nightmode_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    nightmode_switch.setChecked(true);
                    sp.setNightMode(true);
                }else {
                    getDelegate();
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    nightmode_switch.setChecked(false);
                    sp.setNightMode(false);
                }
            }
        });
    }

}

