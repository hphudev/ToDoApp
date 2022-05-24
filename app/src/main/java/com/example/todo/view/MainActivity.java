package com.example.todo.view;

import static com.example.todo.view.LoginActivity.mAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.todo.R;
import com.example.todo.model.library.CustomAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements CustomAlertDialog.alertDialogInterface {

    FloatingActionButton btnSignOut;
    private int CODE_CHECK_EMAIL_VERIFY = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkEmailVerify();
        btnSignOut = (FloatingActionButton)findViewById(R.id.btn_sign_out);
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        GoogleSignOut();
                    }
                }, 500);
            }
        });
    }

    private void checkEmailVerify()
    {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (!firebaseUser.isEmailVerified())
        {
            CustomAlertDialog customAlertDialog = new CustomAlertDialog("Xác minh tài khoản","Một email sẽ được gử cho bạn", CODE_CHECK_EMAIL_VERIFY);
            customAlertDialog.show(getSupportFragmentManager(), "dialog");
        }
    }

    private void GoogleSignOut() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null)
        {
            LoginActivity.googleSignInClient.signOut();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(getBaseContext(), LoginActivity.class));
                    finish();
                }
            }, 500);

        }
    }


    @Override
    public void onPositiveEvent(DialogFragment dialogFragment, int requestCode) {
        if (requestCode == 1)
        {
            FirebaseUser firebaseUser = mAuth.getCurrentUser();
            firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(getBaseContext(), "Email xác nhận đã được gửi", Toast.LENGTH_LONG).show();
                    mAuth.signOut();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(getBaseContext(), LoginActivity.class));
                            finish();
                        }
                    }, 500);
                }
            });
        }
    }

    @Override
    public void onNegativeEvent(DialogFragment dialogFragment, int requestCode) {
        if (requestCode == 1)
        {
            mAuth.signOut();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(getBaseContext(), LoginActivity.class));
                    finish();
                }
            }, 500);
        }
    }
}