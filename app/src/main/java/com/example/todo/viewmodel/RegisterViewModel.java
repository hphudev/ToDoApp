package com.example.todo.viewmodel;

import android.app.Activity;
import android.app.ProgressDialog;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.example.todo.BR;
import com.example.todo.model.data.UserModel;
import com.example.todo.model.library.CustomProgressDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterViewModel extends BaseObservable {
    private String nickname;
    private String email;
    private String password;
    private Activity activity;
    public ObservableField<String> messageRegister = new ObservableField<>();
    public ObservableField <Boolean> isShowMessage = new ObservableField<>();
    public ObservableField <Boolean> isSuccess = new ObservableField<>();

    public RegisterViewModel(Activity activity) {
        this.activity = activity;
    }

    @Bindable
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
        notifyPropertyChanged(BR.nickname);
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

    public void onClickRegister()
    {
        UserModel userModel = new UserModel(getNickname(), getEmail(), getPassword());
        isShowMessage.set(true);
        if (userModel.isValidNickname() && userModel.isValidEmail() && userModel.isValidPassword())
        {
            messageRegister.set("Đang đăng ký...");
            isSuccess.set(true);
            new CustomProgressDialog(this.activity, ProgressDialog.STYLE_SPINNER, "Hệ thống đang đăng ký cho bạn...").show();
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    CustomProgressDialog.dismiss();
                    if (task.isSuccessful()){
                        messageRegister.set("Tài khoản đã được đăng ký thành công");
                    }
                    else
                    {
                        messageRegister.set("Hệ thống gặp sự cố không thể đăng ký");
                    }
                }
            });
        }
        else
        {
            messageRegister.set("Thông tin đăng ký chưa hợp lệ");
            isSuccess.set(false);
        }
    }

}
