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

import java.util.Locale;
import java.util.Random;

//https://developer.android.com/guide/fragments/communicate

/**
 * Home screen (HBar chart) providing user access to its information and most recent test results, if any
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private ProfileGraphFragment graph;
    private BarChart chart;
    private TextView nameTextView, ageTextView, heightTextView, weightTextView, lastnameTextView, counterTextView;
    private ImageView profileImage;
    private SharedPreferenceController sp;
    private Uri picture;
    @SuppressLint("NewApi")
    private static final Instant start_of_counter = Instant.now();

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
        //UserDataViewModel sampleModel = new ViewModelProvider(this, new ViewModelFactory(new MockUpRepository(MockUpServiceBuilder.create(MockUpService.class)))).get(UserDataViewModel.class);

        /*
        Creates or gets instance of the view models used by this fragment
         */
        UserDataViewModel sampleModel = new ViewModelProvider(requireActivity()).get(UserDataViewModel.class);
        DashboardViewModel dashboardViewModel = new ViewModelProvider(requireActivity()).get(DashboardViewModel.class);

        dashboardViewModel.getData().observe(getViewLifecycleOwner(), aFloat -> {
            if(aFloat > 0.0f)
                timer(aFloat);
        });

        /*
        Sets UI components based on new values stored in the View Model
         */

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

        /*
        Inflates the HBar graph fragment
         */
        transaction.replace(R.id.fragmentContainerView, profileGraphFragment).commit();

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


        Intent intent = null;

        try {
            intent = Intent.getIntentOld("comesFrom"); //https://stackoverflow.com/questions/21953839/how-to-decide-which-activity-we-came-from
            String checking = intent.getStringExtra("comesFrom");
            Log.d("HomeFragment", checking);
            if(!checking.equals("Dashboard"))
                counterTextView.setText("");
            else
            {
                timer(sp.getUserData());
            }
        } catch (URISyntaxException | NullPointerException e) {
            e.printStackTrace();
        }



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

    /**
     * Get and sets profile picture for user
     */
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

            //CODE FROM https://www.youtube.com/watch?v=7p4MBsz__ao&list=LL&index=2&t=43s&ab_channel=CodewithLove%28RSTechnoSmart%29
            //has been adapted to work with the requirements of this project. The google firebase documentation has also been used.
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

    //CODE FROM https://www.youtube.com/watch?v=7p4MBsz__ao&list=LL&index=2&t=43s&ab_channel=CodewithLove%28RSTechnoSmart%29
    //has been adapted to work with the requirements of this project. The google firebase documentation has also been used.
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
    //CODE FROM https://stackoverflow.com/questions/4927856/how-can-i-calculate-a-time-difference-in-java Has been updated for this specific use.
    private long timeDifferenceFromStart(Instant start_of_counter, Instant start)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Duration.between(start_of_counter, start).toMillis();
        }else{
            return 0;
        }
    }


    /**
     * Verifies input bac value and starts timer for user if required
     * Function for time required until user reaches value is based in a constant
     * However, ideally this equation should take in account the user's weight, age and height
     * It is set to equal to 0.08% BAC which is the legal limit in Quebec and was rearanged to find the time.
     * The equation is derived from the Widmark's formula:
     * J. Searle, “Alcohol calculations and their uncertainty” Medicine, science, and the law, Jan-2015.
     * [Online]. Available: https://www.ncbi.nlm.nih.gov/pmc/articles/PMC4361698/. [Accessed: 14-Jun-2022].
     */


    @SuppressLint("NewApi")
    public void timer(float a)
    {
        double bac = a;
        double time = ((-0.08 + bac) / 0.015);

        System.out.println("This is the time" + time);


        time = time * 3600;

        if(bac < 0.08)
        {
            String message = "You are safe to drive.";

            counterTextView.setText(message);
        }
        else if (0.08 <= bac && bac <= 0.37)
        {
            start = Instant.now();
            if (start_of_counter != null && start != null)
            {
                System.out.println("This is the start: " + start);
                System.out.println("This is the start: " + start_of_counter);
                long difference = timeDifferenceFromStart();
                if (difference > 0)
                {
                    time = (time - (difference / 1000));
                }
            }

            new CountDownTimer((long)time * 1000,1000) // CODE FROM https://www.geeksforgeeks.org/countdowntimer-in-android-with-example/
                    // Has been updated for this specific use.
            {
                @Override
                public void onTick(long l) {
                    NumberFormat f = new DecimalFormat("00");
                    long hour, minute;
                    hour = (l/3600000) % 24;
                    minute = (l/60000) % 60;
                    counterTextView.setText(f.format(hour) + " h, " + f.format(minute) + " m.");

                }
                @Override
                public void onFinish() {
                    String message = "Please breathe again before taking the wheel";
                    counterTextView.setText(message);
                }
            }.start();
        }
        else
        {
            String message = "SEEK MEDICAL ASSISTANCE";
            counterTextView.setText(message);
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onPause()
    {
        super.onPause();
    }

    @SuppressLint("NewApi")
    public long timeDifferenceFromStart() //CODE FROM https://stackoverflow.com/questions/4927856/how-can-i-calculate-a-time-difference-in-java Has been updated for this specific use.
    {
        Duration timeElapsed = Duration.between(start_of_counter, start);
        return timeElapsed.toMillis();
    }
}