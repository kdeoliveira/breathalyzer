package com.coen390.abreath.data.entity;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.coen390.abreath.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Entity representing the data fetched from mock API
 * The response body will be serialized to this class
 */
public class UserDataEntity {
    private String username;
    private float bac;
    @SerializedName("createdAt")
    private Date created_at;
    private static String result[] = {"","","",""};

    private int id;
    private String name;
    @SerializedName("lastName")
    private String last_name;
    private String email, password, phone;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    private int age;
    private int weight;
    private float height;

    private String ageString;
    private String weightString;
    private String heightString;

    public int getId() {
        return id;
    }

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

    public UserDataEntity(String name, String height, String weight, String age, String phone) //For the account page.
    {
        this.name = name;
        this.phone = phone;
        this.weightString = weight;
        this.ageString = age;
        this.heightString = height;
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

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
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

    public void createAccount()
    {
        FirebaseAuth auth;
        auth = FirebaseAuth.getInstance();


       auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
           @Override
           public void onComplete(@NonNull Task<AuthResult> task) {
               if(task.isSuccessful())
               {
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

    public void signOut()
    {
        FirebaseAuth.getInstance().signOut();
    }

    public void updateDataSettings(Boolean control[])
    {
        DatabaseReference dr;
        dr = FirebaseDatabase.getInstance().getReference().child("user").getRef();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        String passName = name;
        String passWeight = weightString;
        String passHeight = heightString;
        String passAge = ageString;
        String passPhone = phone;

        if (control[0] == true)
            dr.child(uid).child("name").setValue(passName);
        if (control[1] == true)
            dr.child(uid).child("height").setValue(passHeight);
        if (control[2] == true)
            dr.child(uid).child("weight").setValue(passWeight);
        if (control[3] == true)
            dr.child(uid).child("age").setValue(passAge);
        if (control[4] == true)
            dr.child(uid).child("phone").setValue(passPhone);
    }

    public void getDataForHome()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference auth = FirebaseDatabase.getInstance().getReference().child("user");

        auth.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                result[0] = snapshot.child("name").getValue(String.class);
                result[1] = snapshot.child("age").getValue(String.class);
                result[2] = snapshot.child("height").getValue(String.class);
                result[3] = snapshot.child("weight").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public String[] getData()
    {
        return result;
    }
}
