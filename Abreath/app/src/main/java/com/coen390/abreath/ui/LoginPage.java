package com.coen390.abreath.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.coen390.abreath.R;
import com.coen390.abreath.databinding.FragmentHomeBinding;
import com.coen390.abreath.databinding.FragmentLoginPageBinding;


public class LoginPage extends Fragment {

    private FragmentLoginPageBinding binding;
    protected EditText emailLogin, passwordLogin;
    protected Button buttonLogin;
    protected TextView forgotPWordText,signUpLogText, noAccountText;
    private ImageView breathLogo, appALogo;

    public LoginPage() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        emailLogin = binding.loginEmail;
        passwordLogin = binding.loginPassword;
        buttonLogin = binding.loginButton;
        forgotPWordText = binding.forgotPwordText;
        signUpLogText = binding.signupLogin;
        noAccountText = binding.noAccountText;
        breathLogo = binding.breathLogo;
        appALogo = binding.appALogo;


        return inflater.inflate(R.layout.fragment_login_page, container, false);
    }
}