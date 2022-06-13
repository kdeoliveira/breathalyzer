package com.coen390.abreath.ui.settings.pages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.TextView;

import com.coen390.abreath.R;

import java.util.Objects;

public class HelpPage extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_page);


        SharedPreferences night = getSharedPreferences("night",0);
        boolean booleanValue = night.getBoolean("night_mode",false);
        if (booleanValue){
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                    .getColor(R.color.primaryColor)));
        }

        Objects.requireNonNull(getSupportActionBar()).setElevation(0f);
    }
}