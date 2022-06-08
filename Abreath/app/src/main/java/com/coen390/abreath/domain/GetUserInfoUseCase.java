package com.coen390.abreath.domain;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.coen390.abreath.data.entity.UserDataEntity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.internal.IdTokenListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.internal.InternalTokenResult;

import java.util.Objects;
import java.util.Observable;

public class GetUserInfoUseCase implements UseCase {
    private final DatabaseReference mFirebaseRepository;

    public GetUserInfoUseCase(){
        this.mFirebaseRepository = FirebaseDatabase.getInstance().getReference();
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

    public MutableLiveData<UserDataEntity> call(@Nullable Object payload) {
        MutableLiveData<UserDataEntity> usersDataEntity = new MutableLiveData<>();
        FirebaseAuth.getInstance().addIdTokenListener((FirebaseAuth.IdTokenListener) firebaseAuth -> {
            if(firebaseAuth.getUid() != null){
                mFirebaseRepository.child("user").child(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        usersDataEntity.setValue(snapshot.getValue(UserDataEntity.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        return usersDataEntity;
    }
}
