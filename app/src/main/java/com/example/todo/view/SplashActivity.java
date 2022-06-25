package com.example.todo.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.airbnb.lottie.LottieAnimationView;
import com.example.todo.R;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        Objects.requireNonNull(getSupportActionBar()).hide(); // hide the title bar
    }

    @Override
    protected void onResume() {
        super.onResume();
        LottieAnimationView lottieAnimationView = (LottieAnimationView)findViewById(R.id.animation_view);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                lottieAnimationView.pauseAnimation();
                lottieAnimationView.setVisibility(View.GONE);
                Intent intent = new Intent( getBaseContext(), LoginActivity.class);
                startActivity(intent);
                finish();

            }
        }, 5000);
    }
}