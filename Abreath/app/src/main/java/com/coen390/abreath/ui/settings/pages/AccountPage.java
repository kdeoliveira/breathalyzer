package com.coen390.abreath.ui.settings.pages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.coen390.abreath.R;

import java.util.Objects;

public class AccountPage extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_page);



        Objects.requireNonNull(getSupportActionBar()).setElevation(0f);
    }
}