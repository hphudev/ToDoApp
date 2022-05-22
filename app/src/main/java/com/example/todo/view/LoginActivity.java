package com.example.todo.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todo.R;
import com.example.todo.databinding.ActivityLoginBinding;
import com.example.todo.viewmodel.LoginViewModel;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;

import com.example.todo.model.library.*;
import com.google.firebase.auth.UserInfo;

public class LoginActivity extends AppCompatActivity {

    private TextView openRegisterActivity;
    private LoginViewModel loginViewModel;
    private ActivityLoginBinding activityLoginBinding;
    public static GoogleSignInClient googleSignInClient;
    public static FirebaseAuth mAuth;
    public static String TAG_GOOGLE = "GOOGLE_SIGN_IN_TAG";
    public static int RC_SIGN_IN = 1;

    //
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        loginViewModel = new LoginViewModel(this);
        activityLoginBinding.setLoginViewModel(loginViewModel);
        init();
        init_google();
    }

    private void init_google() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_web_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        mAuth = FirebaseAuth.getInstance();
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
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null)
        {
            mAuth = FirebaseAuth.getInstance();
            mAuth.getAccessToken(false).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                @Override
                public void onSuccess(GetTokenResult getTokenResult) {
                    String strProvider = getTokenResult.getSignInProvider();
                    Toast.makeText(getBaseContext(), strProvider, Toast.LENGTH_SHORT).show();
                    if (strProvider.equals("google.com"))
                    {
                        loginViewModel.onClickGoogleSignIn();
                    }
//
//                    So, if (strProvider.equals("password")) then the authentication is by Email + Password,
//                    if (strProvider.equals("google.com")) then the authentication is via Google,
//                    if (strProvider.equals("facebook.com")) then the authentication is via Facebook.
                }
            });

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        CustomProgressDialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN)
        {
            Log.d(TAG_GOOGLE, "Google sign in intent result");
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = accountTask.getResult(ApiException.class);
                firebaseAuthWithGoogleAccount(account);
            }
            catch (Exception e)
            {
                Log.d(TAG_GOOGLE, "onActivityResut: " + e.getMessage());
                CustomProgressDialog.dismiss();
            }
        }
    }

    private void firebaseAuthWithGoogleAccount(GoogleSignInAccount account) {
        Log.d(TAG_GOOGLE, "firebaseAuthWithGoogleAccount: begin firebase auth with google account");
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.d(TAG_GOOGLE, "onSuccess: Logged In");
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        String userID = firebaseUser.getUid();
                        String userEmail = firebaseUser.getEmail();
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG_GOOGLE, "onSuccess: Logged failed");
                        CustomProgressDialog.dismiss();
                    }
                });
    }
}