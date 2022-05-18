package com.example.todo.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.todo.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
    }
}