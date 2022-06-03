package com.coen390.abreath.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.coen390.abreath.R;
import com.coen390.abreath.databinding.FragmentLoginPageBinding;
import com.coen390.abreath.databinding.FragmentRegistrationPageBinding;


public class RegistrationPage extends Fragment {

    private FragmentRegistrationPageBinding binding;
    protected EditText nameSignup, emailSignup, passwordSignup, passwordConfirmSignUp;
    protected Button buttonSignUp;
    protected TextView createAccount,logOnSignUpText, haveAccountText;
    private CheckBox termCheckBox;
    private ImageView breathLogo, appALogo;

    public RegistrationPage() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        createAccount = binding.ceateAccountText;
        haveAccountText = binding.haveAccountText;

        nameSignup = binding.signupName;
        emailSignup = binding.signupEmail;
        logOnSignUpText = binding.loginOnSignup;
        passwordSignup = binding.signupPassword;
        passwordConfirmSignUp = binding.signupConfirmPassword;
        termCheckBox = binding.checkBoxTerms;
        buttonSignUp = binding.signupButton;
        breathLogo = binding.breathLogo;
        appALogo = binding.appALogo;



        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration_page, container, false);
    }
}