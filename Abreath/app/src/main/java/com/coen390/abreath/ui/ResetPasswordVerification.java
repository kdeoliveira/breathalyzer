package com.coen390.abreath.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.coen390.abreath.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class ResetPasswordVerification extends AppCompatActivity {

    protected EditText confirmPword, confirmEmail;
    protected Button buttonConfirmPword;
    protected TextView forgot_pword;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_verification);

        confirmPword = findViewById(R.id.confirm_password);
        confirmEmail = findViewById(R.id.confirm_email);
       buttonConfirmPword = findViewById(R.id.confirm_pword_button);
        forgot_pword = findViewById(R.id.forgot_pword_text);

        mAuth = FirebaseAuth.getInstance();



        forgot_pword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openForgotPassword();
            }
        });

        buttonConfirmPword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPassword();
            }
        });
        Objects.requireNonNull(getSupportActionBar()).setElevation(0f);


    }

    //resource: https://firebase.google.com/docs/auth/web/password-auth
    private void verifyPassword() {
        String checkPass = confirmPword.getText().toString();

        if(checkPass.isEmpty()){
            Toast.makeText(getApplicationContext(), "Please enter a password", Toast.LENGTH_LONG).show();
        }
        else {
            if(confirmEmail.getText().length() > 0 && confirmPword.getText().length() > 0){
                FirebaseAuth auth;
                auth = FirebaseAuth.getInstance();
                auth.signInWithEmailAndPassword(confirmEmail.getText().toString(), confirmPword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            FirebaseUser user = auth.getCurrentUser();
                            //openForgotPassword();
                            openResetPassword();
                        }
                        else
                        {
                            System.out.println(task.getException().getMessage());
                            Toast.makeText(ResetPasswordVerification.this, "Login Not Successful", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    private void openForgotPassword() {
        Intent intent = new Intent(this, ForgotPassword.class);
        startActivity(intent);
    }

    private void openResetPassword(){
        Intent intent = new Intent(this, ResetPassword.class);
        startActivity(intent);
    }


}
