package com.coen390.abreath.ui;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.Toast;

import com.coen390.abreath.MainActivity;
import com.coen390.abreath.R;
import com.coen390.abreath.data.entity.UserDataEntity;
import com.coen390.abreath.ui.settings.SettingsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class ResetPassword extends AppCompatActivity {

    protected EditText newPassword, confirmNewPassword;
    protected Button buttonSavePass;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        newPassword = findViewById(R.id.new_password);
        confirmNewPassword = findViewById(R.id.confirm_new_password);
        buttonSavePass = findViewById(R.id.save_pword_button);
        mAuth = FirebaseAuth.getInstance();

        Objects.requireNonNull(getSupportActionBar()).setElevation(0f);
        getSupportActionBar().setTitle(null);

        buttonSavePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!newPassword.getText().toString().equals(confirmNewPassword.getText().toString())) {
                    System.out.println(newPassword.getText().toString());
                    System.out.println(confirmNewPassword.getText().toString());
                    Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else {

                updatePassword();}


            }
        });




    }

    private void openSettingsPage(){
        Intent intent = new Intent(this, SettingsFragment.class);
        startActivity(intent);
    }


    public void openMain()
    {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.putExtra("login_result", true);
        startActivity(intent);
    }

//Resource: https://github.com/firebase/snippets-android/blob/77870d2c0f6654b5a7582d2d647495c49b04afd5/auth/app/src/main/java/com/google/firebase/quickstart/auth/MainActivity.java#L154-L165

    private void updatePassword(){

            // [START update_password]
            FirebaseUser user = mAuth.getCurrentUser();
            String newPass = newPassword.getText().toString();

            user.updatePassword(newPass)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User password updated.");
                                Toast.makeText(getApplicationContext(), "Password successfully updated", Toast.LENGTH_SHORT).show();
                                openMain();
                            }
                        }
                    });
            // [END update_password]


    }

}

