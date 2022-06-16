package com.coen390.abreath.domain;

import androidx.annotation.Nullable;

import com.coen390.abreath.data.entity.UserDataEntity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Update user account data in the Firebase Repository
 * User must be logged in for such operation to succeed
 */
public class UpdateDataSettingsUseCase implements UseCase{
    private final DatabaseReference mFirebaseRepository;
    private final Boolean[] mControl;
    public UpdateDataSettingsUseCase(Boolean[] control){
        this.mFirebaseRepository = FirebaseDatabase.getInstance().getReference();
        this.mControl = control;
    }
    public Object call(@Nullable Object payload) {
        if(!(payload instanceof UserDataEntity)) return null;

        FirebaseAuth.getInstance().addIdTokenListener((FirebaseAuth.IdTokenListener) firebaseAuth -> {
            if(firebaseAuth.getUid() != null){
                DatabaseReference dr = mFirebaseRepository.child("user").child(firebaseAuth.getUid());

                if(mControl[0])
                    dr.child("name").setValue(((UserDataEntity) payload).getName());
                if(mControl[1])
                    dr.child("lastname").setValue(((UserDataEntity) payload).getLastname());
                if(mControl[2])
                    dr.child("height").setValue(((UserDataEntity) payload).getHeightString());
                if(mControl[3])
                    dr.child("weight").setValue(((UserDataEntity) payload).getWeightString());
                if(mControl[4])
                    dr.child("age").setValue(((UserDataEntity) payload).getAgeString());
                if(mControl[5])
                    dr.child("phone").setValue(((UserDataEntity) payload).getPhone());
            }
        });
        return true;
    }
}
