package com.coen390.abreath.ui.settings;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.coen390.abreath.R;
import com.coen390.abreath.data.entity.UserDataEntity;
import com.coen390.abreath.SensorActivity;
import com.coen390.abreath.databinding.FragmentSettingsBinding;
import com.coen390.abreath.ui.Login;
import com.coen390.abreath.ui.Registration;
import com.coen390.abreath.ui.model.SettingsViewModel;
import com.coen390.abreath.ui.settings.pages.AboutPage;
import com.coen390.abreath.ui.settings.pages.Account;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    protected ListView list;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        list = binding.listView;
        ArrayList<Category> al = new ArrayList<>();
        al.add(new Category(R.drawable.account, "Account"));
        al.add(new Category(R.drawable.switcher, "Apperance"));
        al.add(new Category(R.drawable.shield, "Privacy & Security"));
        al.add(new Category(R.drawable.graph, "Units"));
        al.add(new Category(R.drawable.help, "Help"));
        al.add(new Category(R.drawable.info, "About"));
        al.add(new Category(R.drawable.logout, "Logout"));

        SettingsAdapter sa = new SettingsAdapter(getActivity().getApplicationContext(), R.layout.row, al);
        list.setAdapter(sa);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i)
                {
                    case 0:
                        openAccount();
                        break;
                    case 5:
                        openAboutPage();
                        break;
                    case 6:
                        FirebaseAuth.getInstance().signOut();
                        openSignIn();
                        break;
                    default:
                        break;
                }
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void openAboutPage()
    {
        Intent intent = new Intent(getActivity(), AboutPage.class);
        startActivity(intent);
    }

    public void openSignIn()
    {
        Intent intent = new Intent(getActivity(), Login.class);
        startActivity(intent);
    }

    public void openSignUp()
    {
        Intent intent = new Intent(getActivity(), Registration.class);
        startActivity(intent);
    }

    public void openAccount()
    {
        Intent intent = new Intent(getActivity(), Account.class);
        startActivity(intent);

    }

}