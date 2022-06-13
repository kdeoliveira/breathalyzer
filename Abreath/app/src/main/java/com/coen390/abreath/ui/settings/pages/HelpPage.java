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

    protected TextView help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_page);

        SharedPreferences night = getSharedPreferences("night",0);
        boolean booleanValue = night.getBoolean("night_mode",true);
        if (booleanValue){
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                    .getColor(R.color.primaryColor)));
        }

        help = findViewById(R.id.helpText);

        help.setText("\n\nABreath is an Android application to help you make the\n\n right choice!\n\n\n When you drink, always make sure it is safe for you to drive\n\n before taking the wheel. \n\n\n Alcohol Levels are represented in % of BAC.\n\n BAC represents the \' Blood Alcohol Content\' in your body.\n\n\n If you are over 0.08% BAC, you are legally not allowed to\n\n drive.\n\n\n Simply blow into the breathalyzer for a few seconds\n\n while holding the Analysis button to get your results.");
        //help.setTextColor(Color.BLACK);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0f);
    }
}