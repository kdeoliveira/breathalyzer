package com.coen390.abreath.ui.settings.pages;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.coen390.abreath.R;

import java.util.Objects;

public class AboutPage extends AppCompatActivity {

    protected TextView about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_page);



        about = findViewById(R.id.aboutText);

        about.setText("Abreath is a BreathAlyzer created and developed by:\n\n\n" +
                "Matthieu Pourrat \n\n Jiaxuan Zhao \n\n Minnelle Zafar,\n \n" +
                " Kevin de Oliveira\n\n Antoine Gaubil,\n\n" +
                "KunYi Wang\n\n Noah Louvet \n\n Li Xingze\n" +
                "\n\n\n" +
                "All icons used on the settings page are from Google Icons \n\nand are free to use under the Apache Licence Version 2.0.");

        Objects.requireNonNull(getSupportActionBar()).setElevation(0f);
    }
}