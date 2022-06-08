package com.coen390.abreath.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.coen390.abreath.data.entity.UserDataEntity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class UserSignInUseCase implements UseCase {
    private final FirebaseAuth mFierFirebaseAuth;
    public UserSignInUseCase(){
        this.mFierFirebaseAuth = FirebaseAuth.getInstance();
    }
    @Override
    public <S> S call(@Nullable Object payload) {
        if(payload == null ||!(payload instanceof HashMap)) return null;

        mFierFirebaseAuth.signInWithEmailAndPassword(((HashMap<String, String>) payload).get("email"), ((HashMap<String, String>) payload).get("password")).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
//                if(task.isSuccessful())
//                    FirebaseUser user = mFierFirebaseAuth.getCurrentUser();
//
            }
        });

        return null;
    }
}
