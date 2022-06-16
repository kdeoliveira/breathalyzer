package com.coen390.abreath.domain;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.coen390.abreath.data.entity.TestResultEntity;
import com.coen390.abreath.data.entity.UserDataEntity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Fetch and returns list of the last 12 test results from the Firebase repository for a given user
 */
public class GetLastLevelsUseCase implements UseCase{
    private final DatabaseReference mFirebaseRepository;

    public GetLastLevelsUseCase(){
        this.mFirebaseRepository = FirebaseDatabase.getInstance().getReference();;
    }

    @Override
    public MutableLiveData<List<TestResultEntity>> call(@Nullable Object payload) {
        MutableLiveData<List<TestResultEntity>> testResultData = new MutableLiveData<>();
        //Verifies if user is properly authenticated before accessing data
        FirebaseAuth.getInstance().addIdTokenListener((FirebaseAuth.IdTokenListener) firebaseAuth -> {
            if(firebaseAuth.getUid() != null){
                List<TestResultEntity> testResultEntities = new ArrayList<>();
//                mFirebaseRepository.child("recordings").getRef().child(firebaseAuth.getUid()).removeValue();

                mFirebaseRepository.child("recordings").child(firebaseAuth.getUid()).limitToLast(12).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            if(dataSnapshot.getValue() != null) {
                                testResultEntities.add(new TestResultEntity(dataSnapshot.getValue(String.class)));
                            }
                        }

                        testResultData.postValue(testResultEntities);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("[debug]", error.getMessage());
                    }
                });
            }
        });
        return testResultData;
    }
}
