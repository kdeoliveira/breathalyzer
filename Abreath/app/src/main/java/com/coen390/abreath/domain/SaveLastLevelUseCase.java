package com.coen390.abreath.domain;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;

import com.coen390.abreath.data.entity.TestResultEntity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.List;

public class SaveLastLevelUseCase implements UseCase{
    private final DatabaseReference mFirebaseRepository;

    public SaveLastLevelUseCase(){
        this.mFirebaseRepository = FirebaseDatabase.getInstance().getReference();;
    }

    @Override
    public <S> S call(@Nullable Object payload) {
        if(payload == null) return null;

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser == null) return null;

        String dateTime; //Take system time and convert to string.

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dateTime = (java.time.LocalDateTime.now()).toString();

            String date = dateTime.substring(0,10); //Take the first 11 characters and store them in date.
            String time = dateTime.substring(11,19); //Take the last characters and store them in time.
            dateTime = date+" @ "+time; //Format a string that is returned for use in other classes by setting a format date @ time

            dateTime += ","+String.valueOf(payload);

            this.mFirebaseRepository.child("recordings").getRef().child(firebaseUser.getUid()).push().setValue(dateTime);
        }
        return null;
    }
}
