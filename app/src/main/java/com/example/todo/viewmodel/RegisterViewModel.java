package com.example.todo.viewmodel;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.example.todo.BR;
import com.example.todo.model.data.UserModel;
import com.example.todo.model.library.CustomProgressDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

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
            messageRegister.set("??ang ????ng k??...");
            isSuccess.set(true);
            new CustomProgressDialog(this.activity, ProgressDialog.STYLE_SPINNER, "H??? th???ng ??ang ????ng k?? cho b???n...").show();
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        messageRegister.set("??ang ????ng k?? bi???t danh cho b???n");
                        CustomProgressDialog.setMessage("H??? th???ng ??ang ????ng k?? bi???t danh cho b???n...");
                        onRegisteronFirestore();
                    }
                    else
                    {
                        messageRegister.set("H??? th???ng g???p s??? c??? kh??ng th??? ????ng k??");
                    }
                }
            });
        }
        else
        {
            messageRegister.set("Th??ng tin ????ng k?? ch??a h???p l???");
            isSuccess.set(false);
        }
    }

    public void onRegisteronFirestore()
    {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        Map<String, Object> user = new HashMap<>();
        user.put("email", getEmail());
        user.put("biet_danh", getNickname());
        firestore.collection("nguoidung")
                        .add(user)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                CustomProgressDialog.dismiss();
                                if (task.isSuccessful())
                                {
                                    messageRegister.set("T??i kho???n ???? ???????c ????ng k?? th??nh c??ng");
                                    setNickname("");
                                    setEmail("");
                                    setPassword("");
                                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                    if (!firebaseUser.isEmailVerified())
                                    {
                                        firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(activity, "Email x??c nh???n ???? ???????c g???i", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                }
                            }
                        });
    }

}
