package com.coen390.abreath.ui.home;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.coen390.abreath.R;
import com.coen390.abreath.common.Utility;
import com.coen390.abreath.data.api.MockUpRepository;
import com.coen390.abreath.data.api.MockUpService;
import com.coen390.abreath.data.api.MockUpServiceBuilder;
import com.coen390.abreath.data.entity.UserDataEntity;
import com.coen390.abreath.databinding.FragmentHomeBinding;
import com.coen390.abreath.ui.model.UserDataViewModel;
import com.coen390.abreath.ui.model.ViewModelFactory;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

//https://developer.android.com/guide/fragments/communicate
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private ProfileGraphFragment graph;
    private BarChart chart;
    private TextView nameTextView, ageTextView, heightTextView, weightTextView, lastnameTextView, usernameTextView;
    private ImageView profileImage;
    private SharedPreferences sharedPreferences;
    private boolean heightUnits, weigthUnits;
    private Uri picture;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        sharedPreferences = getContext().getSharedPreferences("units", Context.MODE_PRIVATE);
        heightUnits = sharedPreferences.getBoolean("height", false);
        weigthUnits = sharedPreferences.getBoolean("weight", false);



        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        nameTextView = binding.profileName;
        lastnameTextView = binding.profileLastname;
        usernameTextView = binding.profileUsername;
        ageTextView = binding.profileAge;
        heightTextView = binding.profileHeight;
        profileImage = binding.profileImage;
        weightTextView = binding.profileWeight;


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContent.launch("image/*");
            }
        });

        getProfilePicture();


        //Note that this should be moved into onViewCreated to ensure parent activity or this view has been created before setting ViewModels
//        UserDataViewModel sampleModel = new ViewModelProvider(this, new ViewModelFactory(new MockUpRepository(MockUpServiceBuilder.create(MockUpService.class)))).get(UserDataViewModel.class);
        UserDataViewModel sampleModel = new ViewModelProvider(this, new ViewModelFactory(new MockUpRepository(MockUpServiceBuilder.create(MockUpService.class)))).get(UserDataViewModel.class);


        sampleModel.getUserInfo().observe(getViewLifecycleOwner(), userDataEntity -> {
            nameTextView.setText(userDataEntity.getName());
            lastnameTextView.setText(userDataEntity.getLastname());
//            usernameTextView.setText("@".concat(userDataEntity.getUsername()));
            ageTextView.setText(String.format(Locale.CANADA,"%d", userDataEntity.getAge()));
            if(!heightUnits)
                heightTextView.setText(String.format(Locale.CANADA,"%.2f cm", userDataEntity.getHeight()));
            else{
                int[] feet = Utility.cmtoin(userDataEntity.getHeight());
                heightTextView.setText(String.format(Locale.CANADA,"%d' %d''", feet[0], feet[1]));
            }

            if(!weigthUnits)
                weightTextView.setText(String.format(Locale.CANADA,"%d kg", userDataEntity.getWeight()));
            else
                weightTextView.setText(String.format(Locale.CANADA,"%d lbs", (int) Utility.kgtolbs(userDataEntity.getWeight())) );
        });

        return root;


    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Fragment profileGraphFragment = new ProfileGraphFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        transaction.replace(R.id.fragmentContainerView, profileGraphFragment).commit();

    }

    private BarData createChartData() {
        ArrayList<BarEntry> values = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            float x = i;

            float y = 5 + new Random().nextFloat() * (50 - 5);
            values.add(new BarEntry(x, y));
        }

        BarDataSet set1 = new BarDataSet(values, "Tests");

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        return new BarData(dataSets);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        uploadImage();
        getProfilePicture();
    }

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    // Handle the returned Uri
                    if(uri != null)
                    {
                        picture = uri;
                    }

                }
            });

    public void getProfilePicture()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String uid = "";

        if(user != null)
        {
            uid = user.getUid();
            StorageReference storageRef = storage.getReference().child("images").child(uid);
            StorageReference storageRef2 = storage.getReference().child("images/blank.png");

            System.out.println(storageRef);

            try {
                File localFile = File.createTempFile("images", "temp");
                storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Uri uri = Uri.fromFile(localFile);
                        profileImage.setImageURI(uri);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Download of profile picture failed");
                        storageRef2.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Uri uri = Uri.fromFile(localFile);
                                profileImage.setImageURI(uri);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Default Profile Picture not Loaded");
                            }
                        });
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
            Log.d(TAG, "Download not successful");
    }

    public void uploadImage()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        if(user != null)
        {
            String uid = user.getUid();

            if(picture != null)
            {
                StorageReference storageRef2 = storageRef.child("images/"+uid);

                storageRef2.putFile(picture).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "Upload Successful");
                        profileImage.setImageURI(picture);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Upload Failed.");
                    }
                });
            }
            else
                System.out.println("Not Successful");
        }
        else{
            System.out.println("User not Signed In");
        }

    }

}