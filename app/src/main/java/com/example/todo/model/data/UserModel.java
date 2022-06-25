package com.example.todo.model.data;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.regex.Pattern;

public class UserModel {

    private String nickName;
    private String email;
    private String password;

    public UserModel(String nickName, String email, String password) {
        this.nickName = nickName;
        this.email = email;
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean isValidEmail(){
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isValidPassword(){
        return !TextUtils.isEmpty(password) && password.length() >= 6;
    }

    public boolean isValidNickname()
    {
        return !TextUtils.isEmpty(nickName);
    }
}
