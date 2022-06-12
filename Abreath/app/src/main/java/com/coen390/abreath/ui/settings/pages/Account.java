package com.coen390.abreath.ui.settings.pages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.coen390.abreath.MainActivity;
import com.coen390.abreath.R;
import com.coen390.abreath.common.Utility;
import com.coen390.abreath.data.entity.UserDataEntity;

import com.coen390.abreath.domain.UpdateDataSettingsUseCase;
import com.coen390.abreath.ui.Login;
import com.coen390.abreath.ui.model.SharedPreferenceController;
import com.coen390.abreath.ui.model.UserDataViewModel;

import com.coen390.abreath.ui.settings.SettingsFragment;

import java.util.Locale;

public class Account extends AppCompatActivity {
    private SharedPreferenceController sp;
    protected EditText height_text, weight_text, age_text, phone_text, name_text, lastname;
    protected Button save, delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        getSupportActionBar().setElevation(0f);
        sp = new SharedPreferenceController(this);

        height_text = findViewById(R.id.height);
        weight_text = findViewById(R.id.weight);
        age_text = findViewById(R.id.age);
        phone_text= findViewById(R.id.phone);
        name_text = findViewById(R.id.name);
        save = findViewById(R.id.save);
        lastname = findViewById(R.id.LastName);
        delete = findViewById(R.id.delete_account);


        UserDataViewModel sampleModel = new ViewModelProvider(this).get(UserDataViewModel.class);

        sampleModel.getUserInfo().observe(this, userDataEntity -> {
            if(!sp.getHeight())
                height_text.setText(String.format(Locale.CANADA,"%.2f", userDataEntity.getHeight()));
            else{
                int[] feet = Utility.cmtoin(userDataEntity.getHeight());
                height_text.setText(String.format(Locale.CANADA,"%dft%d", feet[0], feet[1]));
            }
            if(!sp.getWeight())
                weight_text.setText(String.format(Locale.CANADA,"%d", (int)userDataEntity.getWeight()));
            else
                weight_text.setText(String.format(Locale.CANADA,"%d", (int) Utility.kgtolbs(userDataEntity.getWeight())) );

            name_text.setText(userDataEntity.getName());
            lastname.setText(userDataEntity.getLastname());
            phone_text.setText(userDataEntity.getPhone());
            age_text.setText(String.format(Locale.CANADA,"%d", userDataEntity.getAge()));
        });



        Boolean[] control = {true, true, true, true, true, true};

        ToggleButton weight_toggle = (ToggleButton) findViewById(R.id.toggleWeight_account);
        ToggleButton height_toggle = (ToggleButton) findViewById(R.id.toggleHeight_account);

        weight_toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sp.setWeight(b);
            }
        });

        height_toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sp.setHeight(b);

                if(b){
                    height_text.setHint("Height (5ft7)");
                }else{
                    height_text.setHint("Height (170)");
                }
                height_text.setText("");
            }
        });


        weight_toggle.setChecked(sp.getWeight());

        height_toggle.setChecked(sp.getHeight());

        if(sp.getHeight()){
            height_text.setHint("Height (5ft7)");
        }else{
            height_text.setHint("Height (170)");
        }


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String height_value, weight_value;
                if(height_text.getText().toString().isEmpty()){
                    height_value = "";
                }
                else if(sp.getHeight()){
                    String[] feet_inch = height_text.getText().toString().split("ft");
                    try{
                        int feetconv = Integer.parseInt(feet_inch[0]);
                        int inconv = Integer.parseInt(feet_inch[1]);
                        height_value = String.valueOf(Utility.intocm(feetconv, inconv));
                    }
                    catch (NumberFormatException | ArrayIndexOutOfBoundsException e){
                        Toast.makeText(Account.this, "Provide height in this format: 5ft5", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }else{
                    height_value = height_text.getText().toString();
                }

                if(weight_text.getText().toString().isEmpty()){
                    weight_value = "";
                }
                else if(sp.getWeight()){
                    weight_value = String.valueOf(Utility.lbstokg(Float.parseFloat(weight_text.getText().toString())));
                    Log.d("Account", weight_value);
                }else{
                    weight_value = weight_text.getText().toString();
                }

                UserDataEntity ude = new UserDataEntity(name_text.getText().toString(), lastname.getText().toString(), height_value, weight_value, age_text.getText().toString(), phone_text.getText().toString());


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


                new UpdateDataSettingsUseCase(control).call(ude);
                Toast.makeText(Account.this, "Data successfully saved.", Toast.LENGTH_SHORT).show();

                //startActivity(new Intent(Account.this, SettingsFragment.class));

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