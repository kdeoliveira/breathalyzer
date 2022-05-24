package com.coen390.abreath;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.coen390.abreath.databinding.ActivityMainBinding;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomAppBar navView = findViewById(R.id.nav_view);
//        setSupportActionBar(navView);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();


        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.bottomNavView, navController);

//        navView.setFabAlignmentModeAndReplaceMenu();


        binding.fabDashboard.setOnClickListener((View view) -> {
            //https://stackoverflow.com/questions/57529211/intercept-navigationui-onnavdestinationselected-to-make-backstack-pop-with-in
            //Properly navigate from FAB to another fragment using user-defined onNavDestinationSelected behavior
            NavOptions options = new NavOptions.Builder().setPopUpTo(Objects.requireNonNull(navController.getCurrentDestination()).getId(), true).setLaunchSingleTop(true).build();
            navController.navigate(R.id.to_navigation_dashboard, null, options);

        });


    }


//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.navigation_home:
//                NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
//                return NavigationUI.onNavDestinationSelected(item, navController)
//                        || super.onOptionsItemSelected(item);
//
//
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//
//    }
}