package com.example.todo.viewmodel;

import static com.example.todo.view.LoginActivity.RC_SIGN_IN;
import static com.example.todo.view.LoginActivity.TAG_GOOGLE;
import static com.example.todo.view.LoginActivity.googleSignInClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.databinding.ObservableField;

import com.example.todo.BR;
import com.example.todo.R;
import com.example.todo.model.data.UserModel;
import com.example.todo.view.LoginActivity;
import com.example.todo.view.MainActivity;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.tasks.OnSuccessListener;
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

    public void onClickLogin()
    {
        UserModel user = new UserModel(getEmail(), getPassword());
        isShowMessage.set(true);
        if (user.isValidEmail() && user.isValidPassword())
        {
            messageLogin.set("Đang tiến hành đăng nhập");
            isSuccess.set(true);
            Intent intent = new Intent(activity, MainActivity.class);
            activity.startActivity(intent);
        }
        else
        {
            messageLogin.set("Email hoặc mật khẩu không hợp lệ");
            isSuccess.set(false);
        }
    }

    public void onClickGoogleSignIn() {
        Log.d(TAG_GOOGLE, "begin Sign in");
        Intent intent = googleSignInClient.getSignInIntent();
        activity.startActivityForResult(intent, RC_SIGN_IN);
    }

}
