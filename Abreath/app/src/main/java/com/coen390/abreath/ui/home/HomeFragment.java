package com.coen390.abreath.ui.home;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.coen390.abreath.R;
import com.coen390.abreath.common.Utility;
import com.coen390.abreath.databinding.FragmentHomeBinding;
import com.coen390.abreath.ui.model.DashboardViewModel;
import com.coen390.abreath.ui.model.SharedPreferenceController;
import com.coen390.abreath.ui.model.UserDataViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

//https://developer.android.com/guide/fragments/communicate
@RequiresApi(api = Build.VERSION_CODES.O)
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private ProfileGraphFragment graph;
    private BarChart chart;
    private TextView nameTextView, ageTextView, heightTextView, weightTextView, lastnameTextView, counterTextView;
    private ImageView profileImage;
    private SharedPreferenceController sp;
    private Uri picture;
    private static final Instant mStartOfCounter = Instant.now();
    private static Instant start;


    @SuppressLint("NewApi")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = new SharedPreferenceController(requireContext());
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {




        SharedPreferences frag = getActivity().getSharedPreferences("whichfrag", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = frag.edit();
        editor.putString("fragment", "home");
        editor.apply();


        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        nameTextView = binding.profileName;
        lastnameTextView = binding.profileLastname;

        ageTextView = binding.profileAge;
        heightTextView = binding.profileHeight;
        profileImage = binding.profileImage;
        weightTextView = binding.profileWeight;
        counterTextView = binding.homeCounter;




        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContent.launch("image/*");
            }
        });

        getProfilePicture();




        //Note that this should be moved into onViewCreated to ensure parent activity or this view has been created before setting ViewModels
//        UserDataViewModel sampleModel = new ViewModelProvider(this, new ViewModelFactory(new MockUpRepository(MockUpServiceBuilder.create(MockUpService.class)))).get(UserDataViewModel.class);
        UserDataViewModel sampleModel = new ViewModelProvider(this).get(UserDataViewModel.class);


        sampleModel.getUserInfo().observe(getViewLifecycleOwner(), userDataEntity -> {
            nameTextView.setText(userDataEntity.getName());
            lastnameTextView.setText(userDataEntity.getLastname());
            ageTextView.setText(String.format(Locale.CANADA,"%d", userDataEntity.getAge()));
            if(!sp.getHeight())
                heightTextView.setText(String.format(Locale.CANADA,"%.2f cm", userDataEntity.getHeight()));
            else{
                int[] feet = Utility.cmtoin(userDataEntity.getHeight());
                heightTextView.setText(String.format(Locale.CANADA,"%d' %d''", feet[0], feet[1]));
            }

            if(!sp.getWeight())
                weightTextView.setText(String.format(Locale.CANADA,"%d kg", (int)userDataEntity.getWeight()));
            else
                weightTextView.setText(String.format(Locale.CANADA,"%d lbs", (int) Utility.kgtolbs(userDataEntity.getWeight())) );
        });

        //FloatingActionButton help_button = (FloatingActionButton) getActivity().findViewById(R.id.help_button_home);
      //  help_button.setOnClickListener(this::onClick);

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

    @SuppressLint("NewApi")
    @Override
    public void onResume() {
        super.onResume();
        uploadImage();
        getProfilePicture();

        timer(sp.getUserData());

//        Intent intent = null;

//        try {
//            intent = Intent.getIntentOld("comesFrom");
//            checking = intent.getStringExtra("comesFrom");
//            Log.d("HomeFragment", checking);
//            if(!checking.equals("Dashboard"))
//                counterTextView.setText("");
//            else
//            {
//                SharedPreferences valueForTimer = requireContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);
//                timer(valueForTimer.getFloat("value", 0.0f));
//            }
//        } catch (URISyntaxException | NullPointerException e) {
//            e.printStackTrace();
//        }





    }


    //CODE FROM https://www.youtube.com/watch?v=7p4MBsz__ao&list=LL&index=2&t=43s&ab_channel=CodewithLove%28RSTechnoSmart%29
    //has been adapted to work with the requirements of this project.
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
                    } //CODE FROM https://www.youtube.com/watch?v=7p4MBsz__ao&list=LL&index=2&t=43s&ab_channel=CodewithLove%28RSTechnoSmart%29
                    //has been adapted to work with the requirements of this project.
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

    //CODE FROM https://www.youtube.com/watch?v=7p4MBsz__ao&list=LL&index=2&t=43s&ab_channel=CodewithLove%28RSTechnoSmart%29
    //has been adapted to work with the requirements of this project.
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
    private long timeDifferenceFromStart(Instant start_of_counter, Instant start) //CODE FROM https://stackoverflow.com/questions/4927856/how-can-i-calculate-a-time-difference-in-java Has been updated for this specific use.
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Duration.between(start_of_counter, start).toMillis();
        }else{
            return 0;
        }
    }

    @SuppressLint("NewApi")
    public void timer(float bac)
    {
//        if(bac == 0.0f){
//            counterTextView.setText("");
//            return;
//        }

        double time = (-0.08 + bac) / 0.015;

        time = time * 3600;

        Log.d("ViewModel", "timer func");
        System.out.println("timer func");

        if(bac < 0.08)
        {
            String message = "You are safe to drive.";
            counterTextView.setText(message);
        }
        else if (0.08 <= bac && bac <= 0.37)
        {
            Log.d("ViewModel", "timer more than 0.08");

            System.out.println("2");
            start = Instant.now();

            if (mStartOfCounter != null && start != null)
            {
                long difference = timeDifferenceFromStart(mStartOfCounter, start);
                if (difference > 0)
                {
                    time = time - (difference/1000f);
                }
            }
            new CountDownTimer((long)time * 1000,1000)
            {
                @Override
                public void onTick(long l) { // CODE FROM https://www.geeksforgeeks.org/countdowntimer-in-android-with-example/ Has been updated for this specific use.
                    NumberFormat f = new DecimalFormat("00");
                    long hour = (l/3600000) % 24;
                    long minute = (l/60000) % 60;
                    counterTextView.setText(f.format(hour) + " h, " + f.format(minute) + " m.");
                }
                @Override
                public void onFinish() {
                    String message = "Please breathe again before taking the wheel.";
                    counterTextView.setText(message);
                }
            }.start();
        }
        else
        {
            Log.d("ViewModel", "seek mdec");
            System.out.println("3");
            String message = "SEEK MEDICAL ASSISTANCE.";
            counterTextView.setText(message);
        }

    }

    @Override
    public void onStop() {
        super.onStop();
    }
}