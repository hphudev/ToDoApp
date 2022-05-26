package com.example.todo.view;

import static com.example.todo.view.LoginActivity.mAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.todo.R;
import com.example.todo.model.data.ItemTaskModel;
import com.example.todo.model.library.CustomAlertDialog;
import com.example.todo.view.adapter.ItemTaskAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CustomAlertDialog.alertDialogInterface {

    FloatingActionButton btnSignOut;
    ImageView imgUserProfile;
    private int CODE_CHECK_EMAIL_VERIFY = 1;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private RecyclerView recycvCustomItem;
    private RecyclerView recycvDefaultItem;
    private ItemTaskAdapter itemTaskAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        checkEmailVerify();

    }

    private void checkEmailVerify()
    {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (!firebaseUser.isEmailVerified())
        {
            CustomAlertDialog customAlertDialog = new CustomAlertDialog("Xác minh tài khoản","Một email sẽ được gử cho bạn", CODE_CHECK_EMAIL_VERIFY);
            customAlertDialog.show(getSupportFragmentManager(), "dialog");
        }
        collapsingToolbarLayout.setTitle(firebaseUser.getEmail());
        Uri uri = firebaseUser.getPhotoUrl();
        Glide.with(this).load(uri).error(R.drawable.ic_baseline_person_24).into(imgUserProfile);
    }

    private void init()
    {
        recycvCustomItem = (RecyclerView)findViewById(R.id.recycv_custom_item);
        recycvDefaultItem = (RecyclerView)findViewById(R.id.recycv_default_item);
        itemTaskAdapter = new ItemTaskAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recycvDefaultItem.setLayoutManager(linearLayoutManager);
        itemTaskAdapter.setData(getListItemTasks());
        recycvDefaultItem.setAdapter(itemTaskAdapter);

        imgUserProfile = (ImageView) findViewById(R.id.img_user_profile);
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
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.appbar_collapse);
    }

    private List<ItemTaskModel> getListItemTasks() {
        List<ItemTaskModel> itemTaskModelList = new ArrayList<>();
        itemTaskModelList.add(new ItemTaskModel(R.drawable.sun_icon, "Ngày của tôi"));
        itemTaskModelList.add(new ItemTaskModel(R.drawable.star_icon, "Quan trọng"));
        itemTaskModelList.add(new ItemTaskModel(R.drawable.plan_icon, "Đã lập kế hoạch"));
        itemTaskModelList.add(new ItemTaskModel(R.drawable.infinite_icon, "Tất cả"));
        itemTaskModelList.add(new ItemTaskModel(R.drawable.checked_icon, "Đã hoàn thành"));
        itemTaskModelList.add(new ItemTaskModel(R.drawable.ic_baseline_person_24, "Đã giao cho tôi"));
        itemTaskModelList.add(new ItemTaskModel(R.drawable.task_icon, "Tác vụ"));
        return itemTaskModelList;
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