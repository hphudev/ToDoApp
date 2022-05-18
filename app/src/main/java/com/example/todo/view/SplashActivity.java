package com.example.todo.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.example.todo.R;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide(); // hide the title bar
    }

    @Override
    protected void onResume() {
        super.onResume();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent( getBaseContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        };
        new Timer().schedule(task, 1000);
    }
}