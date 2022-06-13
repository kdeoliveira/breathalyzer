package com.coen390.abreath.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.coen390.abreath.MainActivity;
import com.coen390.abreath.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class Login extends AppCompatActivity {

    protected EditText emailLogin, passwordLogin;
    protected Button buttonLogin;
    protected TextView forgotPWordText,signUpLogText, noAccountText, sign_up, forgot_pword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailLogin = findViewById(R.id.login_email);
        passwordLogin = findViewById(R.id.login_password);
        buttonLogin = findViewById(R.id.login_button);
        //forgotPWordText = findViewById(R.id.forgot_pword_text);
//        signUpLogText = findViewById(R.id.signup_login);
        noAccountText = findViewById(R.id.no_account_text);
        sign_up = findViewById(R.id.signup_login);
        forgot_pword = findViewById(R.id.forgot_pword_text);

        SharedPreferences night = getSharedPreferences("night",0);
        boolean booleanValue = night.getBoolean("night_mode",true);
        if (booleanValue){
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                    .getColor(R.color.primaryColor)));
        }
        Objects.requireNonNull(getSupportActionBar()).setElevation(0f);
        getSupportActionBar().setTitle(null);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(emailLogin.getText().length() > 0 && passwordLogin.getText().length() > 0){
                    FirebaseAuth auth;
                    auth = FirebaseAuth.getInstance();
                    auth.signInWithEmailAndPassword(emailLogin.getText().toString(), passwordLogin.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                FirebaseUser user = auth.getCurrentUser();
                                openMain();
                            }
                            else
                            {
                                System.out.println(task.getException().getMessage());
                                Toast.makeText(Login.this, "Login Not Successful", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSignUp();
            }
        });

        forgot_pword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openForgotPassword();
            }
        });



    }

    public void openMain()
    {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.putExtra("login_result", true);
        startActivity(intent);
    }

    private void openSignUp()
    {
        Intent intent = new Intent(this, Registration.class);
        startActivity(intent);
    }

    private void openForgotPassword()
    {
        Intent intent = new Intent(this, ForgotPassword.class);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() //https://www.stechies.com/disable-back-button-press/
    {}


}