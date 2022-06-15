package com.coen390.abreath.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.coen390.abreath.R;
import com.coen390.abreath.data.entity.UserDataEntity;
import com.coen390.abreath.ui.settings.pages.Account;

import java.util.Objects;

/**
 * Activity used for allowing user to create new account
 */
public class Registration extends AppCompatActivity {

    protected EditText nameSignup, emailSignup, passwordSignup, passwordConfirmSignUp;
    protected Button buttonSignUp;
    protected TextView createAccount,logOnSignUpText, haveAccountText;
    private CheckBox termCheckBox;
    private ImageView breathLogo, appALogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        nameSignup = findViewById(R.id.signup_name);
        emailSignup = findViewById(R.id.signup_email);
        passwordSignup = findViewById(R.id.signup_password);
        passwordConfirmSignUp = findViewById(R.id.signup_confirm_password);
        termCheckBox = findViewById(R.id.checkBox_terms);
        buttonSignUp = findViewById(R.id.signup_button);
        haveAccountText = findViewById(R.id.login_on_signup);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0f);
        getSupportActionBar().setTitle(getResources().getText(R.string.create_account));

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!passwordSignup.getText().toString().equals(passwordConfirmSignUp.getText().toString())) {
                    System.out.println(passwordSignup.getText().toString());
                    System.out.println(passwordConfirmSignUp.getText().toString());
                    Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
                else if(!termCheckBox.isChecked())
                {
                    Toast.makeText(getApplicationContext(), "You need to agree to the Terms and Privacy Policy to SignUp", Toast.LENGTH_SHORT).show();
                }
                else if(passwordSignup.getText().toString().length() < 6){
                    Toast.makeText(getApplicationContext(), "Password must have at least 6 characters", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    UserDataEntity ude = new UserDataEntity(emailSignup.getText().toString(), passwordSignup.getText().toString(), nameSignup.getText().toString());
                    ude.createAccount();

                    Toast.makeText(getApplicationContext(), "Account Created", Toast.LENGTH_SHORT).show();
                    ude.signIn(); //To sign the user in.
                    openAccount();
                }

            }
        });

        haveAccountText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSignIn();
            }
        });

    }

    private void openAccount()
    {
        Intent intent = new Intent(getApplicationContext(), Account.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("login_result", true);
        startActivity(intent);
    }

    private void openSignIn()
    {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }
}