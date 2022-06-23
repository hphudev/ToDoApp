package com.example.todo.view;

import static com.example.todo.view.LoginActivity.mAuth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.todo.R;
import com.example.todo.databinding.ActivityMainBinding;
import com.example.todo.model.data.ItemTaskListModel;
import com.example.todo.model.interfaces.ItemTaskInterface;
import com.example.todo.model.library.CustomAlertDialog;
import com.example.todo.view.adapter.ItemTaskListAdapter;
import com.example.todo.viewmodel.MainViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CustomAlertDialog.alertDialogInterface, ItemTaskInterface {

    private FloatingActionButton btnSignOut;
    private ImageView imgUserProfile;
    private TextView tvCreateTaskList;
    private int CODE_CHECK_EMAIL_VERIFY = 1;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private RecyclerView recycvCustomItem;
    private RecyclerView recycvDefaultItem;
    private ItemTaskListAdapter itemTaskListAdapterDefault;
    private ItemTaskListAdapter itemTaskListAdapterCustom;
    private ActivityMainBinding activityMainBinding;
    private MainViewModel mainViewModel;
    private List<ItemTaskListModel> itemTaskListModelListDefault;
    private List<ItemTaskListModel> itemTaskListModelListCustom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        itemTaskListAdapterDefault = new ItemTaskListAdapter(this, this, "default");
        itemTaskListAdapterCustom = new ItemTaskListAdapter(this, this, "custom");
        LinearLayoutManager linearLayoutManagerDefault = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        LinearLayoutManager linearLayoutManagerCustom = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recycvDefaultItem.setLayoutManager(linearLayoutManagerDefault);
        recycvCustomItem.setLayoutManager(linearLayoutManagerCustom);
        itemTaskListAdapterDefault.setData(getListItemTasks());
        loadListItemTasksOnFirestore();
        recycvDefaultItem.setAdapter(itemTaskListAdapterDefault);
        recycvCustomItem.setAdapter(itemTaskListAdapterCustom);;
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recycvCustomItem);
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

    private List<ItemTaskListModel> getListItemTasks() {
        itemTaskListModelListDefault = new ArrayList<>();
        itemTaskListModelListDefault.add(new ItemTaskListModel("Today", R.drawable.sun_icon, "Ngày của tôi"));
        itemTaskListModelListDefault.add(new ItemTaskListModel("Important",R.drawable.star_icon, "Quan trọng"));
//        itemTaskListModelListDefault.add(new ItemTaskListModel("Planning", R.drawable.plan_icon, "Đã lập kế hoạch"));
        itemTaskListModelListDefault.add(new ItemTaskListModel("All", R.drawable.infinite_icon, "Tất cả"));
        itemTaskListModelListDefault.add(new ItemTaskListModel("Completed", R.drawable.checked_icon, "Đã hoàn thành"));
        itemTaskListModelListDefault.add(new ItemTaskListModel("MyTask", R.drawable.ic_baseline_person_24, "Đã giao cho tôi"));
        itemTaskListModelListDefault.add(new ItemTaskListModel("Tasks", R.drawable.task_icon, "Tác vụ"));
        return itemTaskListModelListDefault;
    }

    private void loadListItemTasksOnFirestore(){
         itemTaskListModelListCustom= new ArrayList<>();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        Query query = firestore.collection("danhsach").whereEqualTo("Email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null)
                        {
                            return;
                        }
                        itemTaskListModelListCustom.clear();
                        QuerySnapshot querySnapshot = value;
                        for (QueryDocumentSnapshot document : value)
                        {
                            itemTaskListModelListCustom.add(new ItemTaskListModel(R.drawable.ic_baseline_format_list_bulleted_24, document.get("TenDS").toString(), document.getId(), Integer.parseInt(document.get("STT").toString()), Integer.parseInt(document.get("MauNen").toString()), Integer.parseInt(document.get("MauChu").toString())));
                        }
                        Collections.sort(itemTaskListModelListCustom, new Comparator<ItemTaskListModel>() {
                            @Override
                            public int compare(ItemTaskListModel o1, ItemTaskListModel o2) {
                                if (o1.getStt() > o2.getStt()) {
                                    return 1;
                                }
                                else if (o1.getStt() < o2.getStt()) {
                                    return -1;
                                }
                                else {
                                    return 0;
                                }
                            }
                        });
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                itemTaskListAdapterCustom.setData(itemTaskListModelListCustom);
                            }
                        }, 1000);
                    }
                });
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN
            | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            Collections.swap(itemTaskListModelListCustom, fromPosition, toPosition);
            recycvCustomItem.getAdapter().notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        }

        @Override
        public void onMoved(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, int fromPos, @NonNull RecyclerView.ViewHolder target, int toPos, int x, int y) {
            super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
//            mainViewModel.updatePositionTaskList(itemTaskModelList);
        }

        @Override
        public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
            super.onSelectedChanged(viewHolder, actionState);
            if (actionState == ItemTouchHelper.ACTION_STATE_IDLE)
            {
                mainViewModel.updatePositionTaskList(itemTaskListModelListCustom);
            }
        }
    };

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

    @Override
    public void onItemClick(int position, String type) {
        Bundle bundle = new Bundle();
        if (type.equals("custom")) {
            bundle.putString("id", itemTaskListModelListCustom.get(position).getId());
            bundle.putString("title", itemTaskListModelListCustom.get(position).getTitle());
            bundle.putInt("backgroundColor", itemTaskListModelListCustom.get(position).getBackgroundColor());
            bundle.putInt("titleColor", itemTaskListModelListCustom.get(position).getTextColor());
        }
        else{
            bundle.putString("id", itemTaskListModelListDefault.get(position).getId());
            bundle.putString("title", itemTaskListModelListDefault.get(position).getTitle());
            bundle.putInt("backgroundColor", itemTaskListModelListDefault.get(position).getBackgroundColor());
            bundle.putInt("titleColor", itemTaskListModelListDefault.get(position).getTextColor());
        }
        Intent intent = new Intent(this, TaskListActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}