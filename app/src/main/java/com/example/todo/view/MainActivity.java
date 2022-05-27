package com.example.todo.view;

import static com.example.todo.view.LoginActivity.mAuth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.todo.R;
import com.example.todo.databinding.ActivityMainBinding;
import com.example.todo.model.data.ItemTaskModel;
import com.example.todo.model.library.CustomAlertDialog;
import com.example.todo.view.adapter.ItemTaskAdapter;
import com.example.todo.viewmodel.MainViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity implements CustomAlertDialog.alertDialogInterface {

    private FloatingActionButton btnSignOut;
    private ImageView imgUserProfile;
    private TextView tvCreateTaskList;
    private int CODE_CHECK_EMAIL_VERIFY = 1;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private RecyclerView recycvCustomItem;
    private RecyclerView recycvDefaultItem;
    private ItemTaskAdapter itemTaskAdapterDefault;
    private ItemTaskAdapter itemTaskAdapterCustom;
    private ActivityMainBinding activityMainBinding;
    private MainViewModel mainViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainViewModel = new MainViewModel(this);
        activityMainBinding.setMainViewModel(mainViewModel);
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
        mainViewModel.setTitle(firebaseUser.getEmail());
        Uri uri = firebaseUser.getPhotoUrl();
        Glide.with(this).load(uri).error(R.drawable.ic_baseline_person_24).into(imgUserProfile);
    }

    private void init()
    {
//      RecycleView
        recycvCustomItem = (RecyclerView)findViewById(R.id.recycv_custom_item);
        recycvDefaultItem = (RecyclerView)findViewById(R.id.recycv_default_item);
        itemTaskAdapterDefault = new ItemTaskAdapter(this);
        itemTaskAdapterCustom = new ItemTaskAdapter(this);
        LinearLayoutManager linearLayoutManagerDefault = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        LinearLayoutManager linearLayoutManagerCustom = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recycvDefaultItem.setLayoutManager(linearLayoutManagerDefault);
        recycvCustomItem.setLayoutManager(linearLayoutManagerCustom);
        itemTaskAdapterDefault.setData(getListItemTasks());
//        itemTaskAdapterCustom.setData(getListItemTasksOnFirestore());
        loadListItemTasksOnFirestore();
        recycvDefaultItem.setAdapter(itemTaskAdapterDefault);
        recycvCustomItem.setAdapter(itemTaskAdapterCustom);;
//
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
//

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

    private void loadListItemTasksOnFirestore(){
        List<ItemTaskModel> itemTaskModelList = new ArrayList<>();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("danhsach")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null)
                        {
                            return;
                        }
                        itemTaskModelList.clear();
                        for (QueryDocumentSnapshot document : value)
                        {
                            itemTaskModelList.add(new ItemTaskModel(R.drawable.ic_baseline_format_list_bulleted_24, document.get("TenDS").toString()));
                        }
//                        itemTaskAdapterCustom.clear();
                        itemTaskAdapterCustom.setData(itemTaskModelList);
                    }
                });
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