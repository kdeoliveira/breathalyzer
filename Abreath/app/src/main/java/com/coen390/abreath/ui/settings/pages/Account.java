package com.coen390.abreath.ui.settings.pages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.coen390.abreath.MainActivity;
import com.coen390.abreath.R;
import com.coen390.abreath.data.entity.UserDataEntity;
import com.coen390.abreath.ui.Login;
import com.coen390.abreath.ui.settings.SettingsFragment;

public class Account extends AppCompatActivity {

    protected EditText height_text, weight_text, age_text, phone_text, name_text, lastname;
    protected Button save, delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        getSupportActionBar().setElevation(0f);

        height_text = findViewById(R.id.height);
        weight_text = findViewById(R.id.weight);
        age_text = findViewById(R.id.age);
        phone_text= findViewById(R.id.phone);
        name_text = findViewById(R.id.name);
        save = findViewById(R.id.save);
        lastname = findViewById(R.id.LastName);
        delete = findViewById(R.id.delete_account);

        Boolean[] control = {true, true, true, true, true, true};

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserDataEntity ude = new UserDataEntity(name_text.getText().toString(), lastname.getText().toString(), height_text.getText().toString(), weight_text.getText().toString(), age_text.getText().toString(), phone_text.getText().toString());


                String name = name_text.getText().toString();
                String height = height_text.getText().toString();
                String weight = weight_text.getText().toString();
                String age = age_text.getText().toString();
                String phone = phone_text.getText().toString();
                String last = lastname.getText().toString();

                if (name.isEmpty())
                    control[0] = false;
                if (last.isEmpty())
                    control[1] = false;
                if (height.isEmpty())
                    control[2] = false;
                if (weight.isEmpty())
                    control[3] = false;
                if (age.isEmpty())
                    control[4] = false;
                if (phone.isEmpty())
                    control[5] = false;

                ude.updateDataSettings(control);
                Toast.makeText(Account.this, "Data successfully saved.", Toast.LENGTH_SHORT).show();
                openMain();
            }
        });


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserDataEntity ude = new UserDataEntity();
                ude.deleteAccount();
                openSignIn();
            }
        });
    }

    public void openMain()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void openSignIn()
    {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }


}