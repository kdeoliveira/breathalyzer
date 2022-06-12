package com.coen390.abreath.ui.settings.pages;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.coen390.abreath.R;

import java.util.Objects;

public class AboutPage extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_page);



        Objects.requireNonNull(getSupportActionBar()).setElevation(0f);
    }
}