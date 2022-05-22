package com.example.todo.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.example.todo.R;
import com.example.todo.databinding.ActivityRegisterBinding;
import com.example.todo.viewmodel.RegisterViewModel;

public class RegisterActivity extends AppCompatActivity {

    private RegisterViewModel registerViewModel;
    private ActivityRegisterBinding activityRegisterBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();
        activityRegisterBinding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        registerViewModel = new RegisterViewModel(this);
        activityRegisterBinding.setRegisterViewModel(registerViewModel);
    }
}