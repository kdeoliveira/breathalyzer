package com.coen390.abreath;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.coen390.abreath.data.entity.UserDataEntity;
import com.coen390.abreath.ui.Login;
import com.google.android.material.bottomappbar.BottomAppBar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.coen390.abreath.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private static int startup = 0;


    private AppBarConfiguration appBarConfiguration;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(user == null && startup == 0) //user is not signed in.
        {
            startup++;
            openSignIn();
        }

        startup++;
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

//        BottomAppBar navView = findViewById(R.id.nav_view);
//        setSupportActionBar(navView);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.connectionDashboard, R.id.navigation_settings)
                .build();

        Objects.requireNonNull(getSupportActionBar()).setElevation(0f);



        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.bottomNavView, navController);






        binding.fabDashboard.setOnClickListener((View view) -> {
            //https://stackoverflow.com/questions/57529211/intercept-navigationui-onnavdestinationselected-to-make-backstack-pop-with-in
            //Properly navigate from FAB to another fragment using user-defined onNavDestinationSelected behavior
            NavOptions options = new NavOptions.Builder().setPopUpTo(Objects.requireNonNull(navController.getCurrentDestination()).getId(), true).setLaunchSingleTop(false).build();
            navController.navigate(R.id.to_navigation_dashboard, null, options);

        });

    }

    public void openSignIn()
    {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}