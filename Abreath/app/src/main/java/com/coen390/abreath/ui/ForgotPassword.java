package com.coen390.abreath.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.coen390.abreath.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;



public class ForgotPassword extends AppCompatActivity {

    protected EditText emailReset;
    protected Button buttonSendEmail;
    protected TextView login_back;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailReset = findViewById(R.id.reset_email);
        buttonSendEmail = findViewById(R.id.send_email_button);
        login_back = findViewById(R.id.backto_login);

        mAuth = FirebaseAuth.getInstance();

        Objects.requireNonNull(getSupportActionBar()).setElevation(0f);
        getSupportActionBar().setTitle(null);

        login_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLogin();
            }
        });

////reference: https://www.youtube.com/watch?v=0-DRdI_xpvQ
        buttonSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = emailReset.getText().toString();

                if (TextUtils.isEmpty(userEmail)){
                    Toast.makeText(ForgotPassword.this, "Please enter a valid e-mail address.", Toast.LENGTH_SHORT).show();
                }
                else {
                    mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(ForgotPassword.this, "A link to reset your password has been sent to your e-mail.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ForgotPassword.this, Login.class));
                            }
                            else {
                                String messageError = task.getException().getMessage();
                                Toast.makeText(ForgotPassword.this, "Error: " + messageError, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }

    private void openLogin()
    {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }
}