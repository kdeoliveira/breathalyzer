package com.coen390.abreath.data.entity;

import static android.content.ContentValues.TAG;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import androidx.annotation.RequiresApi;


import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicMarkableReference;

/**
 * Entity representing the data fetched from the database
 * The response body will be serialized to this class
 *
 */
public class UserDataEntity {
    private String username;
    private float bac;
    @SerializedName("createdAt")
    private Date created_at;
    private static final String[] result = {"","","",""};
    private static final ArrayList<String> list = new ArrayList<>();

    private int id;
    private String name;
    @SerializedName("lastName")
    private String last_name;
    private String email;
    private String password;

    /*
    GETTERS AND SETTERS
    NOTE: Proper setters/getters required in order for firebase to properly convert its snapshot into this object
     */
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    private String phone;

    public int getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = Integer.parseInt(age);
        this.ageString = age;
    }


    public float getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = Float.parseFloat(weight);
        this.weightString = weight;
    }


    public float getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = Float.parseFloat(height);
        this.heightString = height;
    }

    private int age;
    private float weight;
    private float height;

    private String ageString;
    private String weightString;

    public String getAgeString() {
        return ageString;
    }

    public void setAgeString(String ageString) {
        this.ageString = ageString;
    }

    public String getWeightString() {
        return weightString;
    }

    public void setWeightString(String weightString) {
        this.weightString = weightString;
    }

    public String getHeightString() {
        return heightString;
    }

    public void setHeightString(String heightString) {
        this.heightString = heightString;
    }

    private String heightString;

    public int getId() {
        return id;
    }

    /**
     * Multiple constructor definition since used by firebase and mock up api
     */

    public UserDataEntity(String username, float bac, Date created_at, int id, String name, String last_name, int age, int weight, float height) {
        this.username = username;
        this.bac = bac;
        this.created_at = created_at;
        this.id = id;
        this.name = name;
        this.last_name = last_name;
        this.age = age;
        this.weight = weight;
        this.height = height;
    }

    public UserDataEntity(String email, String password, String name) //For sign up
    {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public UserDataEntity(String email, String password) //For log in
    {
        this.email = email;
        this.password = password;
    }

    public UserDataEntity(String name, String lastname, String height, String weight, String age, String phone) //For the account page.
    {
        this.name = name;
        this.phone = phone;
        this.weightString = weight;
        this.ageString = age;
        this.heightString = height;

        this.last_name = lastname;
        try{
            this.weight = Float.parseFloat(weight);
            this.age = Integer.parseInt(age);
            this.height = Float.parseFloat(height);

        }catch (NumberFormatException ignored){
        }

    }

    public UserDataEntity()//Empty Constructor
    {

    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return last_name;
    }

    public void setLastname(String lastname) {
        this.last_name = lastname;

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public float getBac() {
        return bac;
    }

    public void setBac(float bac) {
        this.bac = bac;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }


    /**
     * Handler for created new account
     * Since this handelr is only used once, no need to move to domain
     */
    public void createAccount()
    {
        FirebaseAuth auth;
        auth = FirebaseAuth.getInstance();

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        String uid = user.getUid();
                        DatabaseReference dr;
                        dr = FirebaseDatabase.getInstance().getReference().child("user").child(uid);
                        String nameString = name;
                        dr.child("name").setValue(nameString);
                    }
                }

            });
    }

    public void signIn()
    {
        FirebaseAuth auth;
        auth = FirebaseAuth.getInstance();

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    FirebaseUser user = auth.getCurrentUser();
                }
                else
                {
                   System.out.println(task.getException().getMessage());
                }
            }
        });
    }

    /**
     * Handler for signing out a user
     * Since this handler is only used once, no need to move to domain
     */
    public void signOut()
    {
        FirebaseAuth.getInstance().signOut();
    }

    /**
     * handler for updating user data
     * Since this handelr is only used once, no need to move to domain
     */
    public void updateDataSettings(Boolean[] control)
    {
        DatabaseReference dr;
        dr = FirebaseDatabase.getInstance().getReference().child("user").getRef();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        if (user != null)
        {
            String uid = user.getUid();
            String passName = name;
            String passWeight = weightString;
            String passHeight = heightString;
            String passAge = ageString;
            String passPhone = phone;
            String passLastName = last_name;
            if (control[0])
                dr.child(uid).child("name").setValue(passName);
            if (control[1])
                dr.child(uid).child("lastname").setValue(passLastName);
            if (control[2]){
                dr.child(uid).child("height").setValue(passHeight);
            }
            if (control[3]){
                dr.child(uid).child("weight").setValue(passWeight);
            }
            if (control[4])
                dr.child(uid).child("age").setValue(passAge);
            if (control[5])
                dr.child(uid).child("phone").setValue(passPhone);
        }
       else
            System.out.println("User is not signed in: null pointer reference");
    }


    /**
     * Handler for deleting user account from the database
     * Since this handelr is only used once, no need to move to domain
     */
    public void deleteAccount()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null)
        {
            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Log.d(TAG, "User Account Deleted");
                    }
                }
            });
        }
    }



    public String[] getData()
    {
        return result;
    }
}
