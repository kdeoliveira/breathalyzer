package com.coen390.abreath.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.coen390.abreath.MainActivity;
import com.coen390.abreath.R;
import com.coen390.abreath.data.entity.UserDataEntity;

public class Login extends AppCompatActivity {

    protected EditText emailLogin, passwordLogin;
    protected Button buttonLogin;
    protected TextView forgotPWordText,signUpLogText, noAccountText, sign_up;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailLogin = findViewById(R.id.login_email);
        passwordLogin = findViewById(R.id.login_password);
        buttonLogin = findViewById(R.id.login_button);
        forgotPWordText = findViewById(R.id.forgot_pword_text);
        signUpLogText = findViewById(R.id.signup_login);
        noAccountText = findViewById(R.id.no_account_text);
        sign_up = findViewById(R.id.signup_login);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserDataEntity ude = new UserDataEntity(emailLogin.getText().toString(), passwordLogin.getText().toString());
                ude.signIn();
                openMain();
            }
        });

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSignUp();
            }
        });

    }

    public void openMain()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void openSignUp()
    {
        Intent intent = new Intent(this, Registration.class);
        startActivity(intent);
    }
}