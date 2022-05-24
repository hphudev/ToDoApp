package com.example.todo.viewmodel;

import static com.example.todo.view.LoginActivity.RC_SIGN_IN;
import static com.example.todo.view.LoginActivity.TAG_GOOGLE;
import static com.example.todo.view.LoginActivity.googleSignInClient;
import static com.example.todo.view.LoginActivity.mAuth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.databinding.ObservableField;

import com.example.todo.BR;
import com.example.todo.R;
import com.example.todo.model.data.UserModel;
import com.example.todo.model.library.CustomProgressDialog;
import com.example.todo.view.LoginActivity;
import com.example.todo.view.MainActivity;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginViewModel extends BaseObservable {
    private String email;
    private String password;
    private Activity activity;
    public ObservableField <String> messageLogin = new ObservableField<>();
    public ObservableField <Boolean> isShowMessage = new ObservableField<>();
    public ObservableField <Boolean> isSuccess = new ObservableField<>();
//
    public LoginViewModel(Activity activity) {
        this.activity = activity;
    }

    @Bindable
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        notifyPropertyChanged(BR.email);
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
    }

    public void onClickLoginWithPassword()
    {
        UserModel user = new UserModel(null, getEmail(), getPassword());
        isShowMessage.set(true);
        if (user.isValidEmail() && user.isValidPassword())
        {
            messageLogin.set("Đang tiến hành đăng nhập");
            isSuccess.set(true);
            new CustomProgressDialog(activity, ProgressDialog.STYLE_SPINNER, "Đang đăng nhập...").show();
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(activity, "Đăng nhập thành công", Toast.LENGTH_LONG).show();
                        goToMainActivity();
                    }
                    else
                    {
                        Toast.makeText(activity, "Đăng nhập thất bại", Toast.LENGTH_LONG).show();
                        CustomProgressDialog.dismiss();
                    }
                }
            });
        }
        else
        {
            messageLogin.set("Email hoặc mật khẩu không hợp lệ");
            isSuccess.set(false);
        }
    }

    public void onClickGoogleSignIn() {
        Log.d(TAG_GOOGLE, "begin Sign in");
        CustomProgressDialog progressDialog = new CustomProgressDialog(activity, ProgressDialog.STYLE_SPINNER, "Đang tiến hành đăng nhập...");
        progressDialog.show();
        Intent intent = googleSignInClient.getSignInIntent();
        activity.startActivityForResult(intent, RC_SIGN_IN);
    }

    public void goToMainActivity()
    {
        activity.startActivity(new Intent(activity, MainActivity.class));
//        activity.finish();
    }

    public void onClickResetPassword()
    {
        UserModel userModel = new UserModel("", getEmail(), "");
        if (!userModel.isValidEmail())
        {
            isShowMessage.set(true);
            isSuccess.set(false);
            messageLogin.set("Vui lòng nhập email ở trên");
        }
        else {
            mAuth.sendPasswordResetEmail(userModel.getEmail())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                isShowMessage.set(true);
                                isSuccess.set(true);
                                messageLogin.set("Hệ thống đã gửi một email cho bạn");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        isShowMessage.set(false);
                                    }
                                }, 5000);
                            }
                        }
                    });
//        mAuth.sendPasswordResetEmail()
        }
    }


}
