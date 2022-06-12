package com.coen390.abreath.ui.settings.pages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.coen390.abreath.R;
import com.coen390.abreath.ui.model.SharedPreferenceController;

import java.util.Objects;

public class UnitsPage extends AppCompatActivity {

    protected TextView units;
    SharedPreferenceController sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_units_page);

        sp = new SharedPreferenceController(this);
        units = findViewById(R.id.unitsText);

        //units.setText(test);
        ToggleButton weight = (ToggleButton) findViewById(R.id.toggleWeight);
        weight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {//lbs
                    sp.setWeight(true);

                } else {
                    sp.setWeight(false);
                }

            }
        });

        ToggleButton height = (ToggleButton) findViewById(R.id.toggleHeight);
        height.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {//inches
                    sp.setHeight(true);
                } else {
                    sp.setHeight(false);
                }

            }
        });

        weight.setChecked(sp.getWeight());

        height.setChecked(sp.getHeight());

        Objects.requireNonNull(getSupportActionBar()).setElevation(0f);
    }
//
//    private String intocm() {
//        float temp = (float) (feet*0.3048 + inch*0.0254);
//        temp *=100;
//        localh = temp;
//        int newtemp = (int) temp;
//        String height_data = newtemp + "cm";
//        SharedPreferences.Editor editor = sp.edit();
//        editor.putString("height",height_data);
//        editor.apply();
//        return height_data;
//    }
//
//    private String cmtoin() {
//        SharedPreferences sp2 = getApplicationContext().getSharedPreferences("units", Context.MODE_PRIVATE);
//        String user_height = sp2.getString("user_height","");
//        localh =  Float.parseFloat(user_height);
//        float length = 100*(localh/100)/2.54f;
//        feet = (int)length/12;
//        inch = length - 12*feet;
//        int newinch = (int)inch;
//        int newfeet = (int)feet;
//        String height_data = newfeet + "ft " + newinch + "in";
//        SharedPreferences.Editor editor = sp.edit();
//        editor.putString("height", height_data);
//        editor.apply();
//        return height_data;
//
//    }
//
//    private String lbstokg() {
//
//        float temp  = localw;
//        //float temp =  Float.parseFloat(weight_data);
//        temp *= 0.45359237f;
//        temp = Math.round(temp);
//        int newtemp = (int)temp;
//        String weight_data = newtemp + "kg";
//        SharedPreferences.Editor editor = sp.edit();
//        editor.putString("weight", weight_data);
//        editor.apply();
//        return weight_data;
//    }
//
//    private String kgtolbs() {
//        SharedPreferences sp2 = getApplicationContext().getSharedPreferences("units", Context.MODE_PRIVATE);
//        String user_weight = sp2.getString("user_weight","");
//        localw =  Float.parseFloat(user_weight);
//        float temp = localw;
//        //float temp = Float.parseFloat(weight);
//        temp *= 2.2046226218f;
//        temp = Math.round(temp);
//        localw = temp;
//        int newtemp = (int)temp;
//        String weight_data = newtemp + "lbs";
//        SharedPreferences.Editor editor = sp.edit();
//        editor.putString("weight", weight_data);
//        editor.apply();
//        return weight_data;
//
//    }


}

