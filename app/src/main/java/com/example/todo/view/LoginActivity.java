package com.example.todo.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.todo.R;
import com.example.todo.databinding.ActivityLoginBinding;
import com.example.todo.viewmodel.LoginViewModel;


public class LoginActivity extends AppCompatActivity {

    private TextView openRegisterActivity;
//
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        ActivityLoginBinding activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        LoginViewModel loginViewModel = new LoginViewModel(LoginActivity.this);
        activityLoginBinding.setLoginViewModel(loginViewModel);
        init();
    }

    private void init()
    {
        openRegisterActivity = (TextView) findViewById(R.id.open_register_activity);
        openRegisterActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}