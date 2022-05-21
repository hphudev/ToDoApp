package com.example.todo.viewmodel;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.databinding.ObservableField;

import com.example.todo.BR;
import com.example.todo.model.data.UserModel;
import com.example.todo.view.LoginActivity;
import com.example.todo.view.MainActivity;


public class LoginViewModel extends BaseObservable {
    private String email;
    private String password;
    private Context context;
    public ObservableField <String> messageLogin = new ObservableField<>();
    public ObservableField <Boolean> isShowMessage = new ObservableField<>();
    public ObservableField <Boolean> isSuccess = new ObservableField<>();

    public LoginViewModel(Context context) {
        this.context = context;
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
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        }
        else
        {
            messageLogin.set("Email hoặc mật khẩu không hợp lệ");
            isSuccess.set(false);
        }
    }


}
