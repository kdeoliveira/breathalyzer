package com.coen390.abreath;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class AboutPage extends AppCompatActivity {

    protected TextView about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_page);



        about = findViewById(R.id.aboutText);

        about.setText("Abreath is a BreathAlyzer created and developed by:\n\n" +
                "Matthieu Pourrat, Jiaxuan Zhao, Minnelle Zafar,\n" +
                " Kevin de Oliveira, Antoine Gaubil,\n" +
                "KunYi Wang, Noah Louvet, Li Xingze\n" +
                "\n\n\n\n\n\n" +
                "All icons used on the settings page are from Google Icons and are free to use under the Apache Licence Version 2.0.");
    }
}