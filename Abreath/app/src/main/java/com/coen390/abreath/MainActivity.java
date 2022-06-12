package com.coen390.abreath;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.coen390.abreath.data.entity.UserDataEntity;
import com.coen390.abreath.service.BleService;
import com.coen390.abreath.service.NetworkManager;
import com.coen390.abreath.ui.Login;
import com.google.android.material.bottomappbar.BottomAppBar;

import androidx.annotation.NonNull;
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
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();;

    private ConnectivityManager.NetworkCallback connectionNetworkCallback;

    private AppBarConfiguration appBarConfiguration;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.connectionDashboard, R.id.navigation_settings)
                .build();

        Objects.requireNonNull(getSupportActionBar()).setElevation(0f);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.bottomNavView, navController);

        connectionNetworkCallback = new ConnectivityManager.NetworkCallback(){
            @Override
            public void onAvailable(@NonNull Network network) {
                Toast.makeText(MainActivity.this, "Reconnected to internet", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onLost(@NonNull Network network) {
                Log.d("MainActivity", "Lost internet connection");
            }
        };

        binding.fabDashboard.setOnClickListener((View view) -> {
            //https://stackoverflow.com/questions/57529211/intercept-navigationui-onnavdestinationselected-to-make-backstack-pop-with-in
            //Properly navigate from FAB to another fragment using user-defined onNavDestinationSelected behavior
            NavOptions options = new NavOptions.Builder().setPopUpTo(Objects.requireNonNull(navController.getCurrentDestination()).getId(), true).setLaunchSingleTop(false).build();
            navController.navigate(R.id.to_navigation_dashboard, null, options);

        });
    }


    @Override
    protected void onStart() {
        super.onStart();
//        Intent intentRecv = getIntent();
        if(user == null){ //&& intentRecv.getBooleanExtra("login_result", false) was used but doesn't secure the app
            Intent intent = new Intent(this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkManager.Builder.create(this).checkConnection(connectionNetworkCallback);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}