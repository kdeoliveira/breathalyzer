package com.coen390.abreath.domain;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.coen390.abreath.common.Tuple;
import com.coen390.abreath.data.api.MockUpRepository;
import com.coen390.abreath.data.entity.UserDataEntity;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Observable;

public class GetUserInfoUseCase extends Observable implements UseCase {
    private final DatabaseReference repository;

    public GetUserInfoUseCase(DatabaseReference ref){
        this.repository = ref;
    }

    @Override
    public Object call(@Nullable Object payload) {

        this.repository.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDataEntity userDataEntity;
                try{
                    userDataEntity  = new UserDataEntity(
                            snapshot.child("name").getValue(String.class),
                            snapshot.child("lastname").getValue(String.class),
                            snapshot.child("height").getValue(String.class),
                            snapshot.child("weight").getValue(String.class),
                            snapshot.child("age").getValue(String.class),
                            snapshot.child("phone").getValue(String.class)
                    );
                }catch (Exception e){
                    userDataEntity = new UserDataEntity();
                }

                setChanged();
                notifyObservers(userDataEntity);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("[debug]", error.getMessage());
            }
        });
        return payload;
    }
}
