package com.example.todo.view;

import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.example.todo.R;
import com.example.todo.databinding.ActivityTaskListBinding;
import com.example.todo.model.data.GroupObjectModel;
import com.example.todo.model.data.ItemTaskModel;
import com.example.todo.model.library.CustomAlertDialog;
import com.example.todo.view.epdadapter.ItemTaskExpandListViewAdapter;
import com.example.todo.viewmodel.TaskListViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.SimpleFormatter;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class TaskListActivity extends AppCompatActivity implements CustomAlertDialog.alertDialogInterface {

    private static final int NOTIFICATION_ID = 1301;
    private static final String CHANNEL_ID = "1301";
    private ActivityTaskListBinding activityTaskListBinding;
    private TaskListViewModel taskListViewModel;
    private Toolbar toolbar;
    private BottomSheetBehavior bottomSheetAddTask;
    private FloatingActionButton fbtnAddTask;
    private LinearLayout layoutBottomSheetAddTask;
//
    private Calendar calendarExpired = Calendar.getInstance();
    private Boolean checkSetCalendarExpired = false;
    private Calendar calendarRemind = Calendar.getInstance();
    private Boolean checkSetCalendarRemind = false;
//
    private ExpandableListView expandableListView;
    private Map<GroupObjectModel, List<ItemTaskModel>> mListItems = new HashMap<>();
    private List<GroupObjectModel> groupObjectModelList = new ArrayList<>(mListItems.keySet());
    List<ItemTaskModel> itemTaskModelListIncompleted = new ArrayList<>();
    List<ItemTaskModel> itemTaskModelListCompleted = new ArrayList<>();
    private ItemTaskExpandListViewAdapter itemTaskExpandListViewAdapter = new ItemTaskExpandListViewAdapter(groupObjectModelList, mListItems);
    private GroupObjectModel groupObjectModel1 = new GroupObjectModel(1, "Incompleted");
    private GroupObjectModel groupObjectModel2 = new GroupObjectModel(2, "Completed");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
//        getSupportActionBar().hide();
        activityTaskListBinding = DataBindingUtil.setContentView(this, R.layout.activity_task_list);
        taskListViewModel = new TaskListViewModel(this);
        activityTaskListBinding.setTaskListViewModel(taskListViewModel);
//
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        taskListViewModel.setTitle(bundle.getString("title"));
        taskListViewModel.setId(bundle.getString("id"));
        String hexColor = String.format("#%06X", (0xFFFFFF & bundle.getInt("backgroundColor")));
        taskListViewModel.setBackgroundColor(Color.parseColor(hexColor));
        hexColor = String.format("#%06X", (0xFFFFFF & bundle.getInt("titleColor")));
        taskListViewModel.setTitleColor(Color.parseColor(hexColor));
        fbtnAddTask = (FloatingActionButton)findViewById(R.id.fbtn_add_task);
        layoutBottomSheetAddTask = (LinearLayout)findViewById(R.id.included_bottom_sheet);
        bottomSheetAddTask = BottomSheetBehavior.from(layoutBottomSheetAddTask);
//
        initBottomSheet();
        initCreateTask();
        initExpandableListView();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = getString(R.string.channel_name);
//            String description = getString(R.string.channel_description);
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
//            channel.setDescription(description);
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
    }

    private void initExpandableListView() {
        expandableListView = findViewById(R.id.expand_list_view_tasks);
        expandableListView.setAdapter(itemTaskExpandListViewAdapter);
        loadExpandableListView();
    }

    private void loadExpandableListView() {
//      Lấy dữ liệu của collection "nhiemvu"
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        Query query;
        switch (taskListViewModel.getId()){
            case "All":
            case "Tasks":
            case "Today":
                query = firestore.collection("nhiemvu")
                        .whereEqualTo("Email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                break;
            case "Completed":
                query = firestore.collection("nhiemvu")
                        .whereEqualTo("Email", FirebaseAuth.getInstance().getCurrentUser().getEmail())
                        .whereEqualTo("TinhTrang", true);
                break;
            case "Important":
                query = firestore.collection("nhiemvu")
                        .whereEqualTo("Email", FirebaseAuth.getInstance().getCurrentUser().getEmail())
                        .whereEqualTo("QuanTrong", true);
                break;
            default:
                query = firestore.collection("nhiemvu")
                        .whereEqualTo("MaDS", taskListViewModel.getId());
                break;
        }
        query
        .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null)
                    return;
                itemTaskModelListIncompleted.clear();
                itemTaskModelListCompleted.clear();
                mListItems.clear();
                int count = 0;
                for (QueryDocumentSnapshot document: value) {
                    if (taskListViewModel.getId().equals("Today"))
                    {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        try {
                            Date dateOnDatabase = simpleDateFormat.parse(document.getString("NhacNho"));
                            if (!simpleDateFormat.format(dateOnDatabase).equals(simpleDateFormat.format(new Date())))
                                continue;
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    count++;
                    if (!document.getBoolean("TinhTrang"))
                        itemTaskModelListIncompleted.add(new ItemTaskModel(count, document.getId().toString(), document.getString("MaDS"), document.getString("TenNV"),
                                document.getString("NgayDenHan"), document.getString("NhacNho"), document.getBoolean("QuanTrong"), document.getBoolean("TinhTrang")));
                    else
                        itemTaskModelListCompleted.add(new ItemTaskModel(count, document.getId(), document.getString("MaDS"), document.getString("TenNV"),
                                document.getString("NgayDenHan"), document.getString("NhacNho"), document.getBoolean("QuanTrong"), document.getBoolean("TinhTrang")));
                }
                if (!taskListViewModel.getId().equals("Completed"))
                    mListItems.put(groupObjectModel1, itemTaskModelListIncompleted);
                mListItems.put(groupObjectModel2, itemTaskModelListCompleted);
                groupObjectModelList = new ArrayList<>(mListItems.keySet());
                itemTaskExpandListViewAdapter.changeData(groupObjectModelList, mListItems);
                expandableListView.expandGroup(0);
                if (!taskListViewModel.getId().equals("Completed"))
                    expandableListView.expandGroup(1);
//
//                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_app);
//                Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                NotificationCompat.Builder builder = new NotificationCompat.Builder(TaskListActivity.this, CHANNEL_ID)
//                        .setSmallIcon(R.drawable.star_on)
//                        .setLargeIcon(bitmap)
//                        .setSound(uri)
//                        .setContentTitle("Đồng bộ dữ liệu")
//                        .setContentText("VnDo đã đồng bộ dữ liệu.")
//                        .setPriority(NotificationCompat.PRIORITY_HIGH)
//                        .setStyle(new NotificationCompat.BigTextStyle()
//                                .bigText("Đang đồng bộ dữ liệu..."));
//                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(TaskListActivity.this);
//                notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());
            }
        });
    }

    private void initCreateTask() {
//  set expired
        activityTaskListBinding.includedBottomSheet.tvDateExpired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendarExpired.set(Calendar.YEAR, year);
                        calendarExpired.set(Calendar.MONTH, month);
                        calendarExpired.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        checkSetCalendarExpired = true;
                    }
                };
                new DatePickerDialog(TaskListActivity.this, date, calendarExpired.get(Calendar.YEAR), calendarExpired.get(Calendar.MONTH), calendarExpired.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
//  set remind
        activityTaskListBinding.includedBottomSheet.tvRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                calendarRemind.set(Calendar.YEAR, year);
                                calendarRemind.set(Calendar.MONTH, month);
                                calendarRemind.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                calendarRemind.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendarRemind.set(Calendar.MINUTE, minute);
                                checkSetCalendarRemind = true;
//                                Toast.makeText(TaskListActivity.this, calendarRemind.toString(), Toast.LENGTH_LONG).show();
                            }
                        };
                        new TimePickerDialog(TaskListActivity.this, time, calendarRemind.get(Calendar.HOUR), calendarRemind.get(Calendar.MINUTE), DateFormat.is24HourFormat(TaskListActivity.this)).show();
                    }
                };
                new DatePickerDialog(TaskListActivity.this, date, calendarRemind.get(Calendar.YEAR), calendarRemind.get(Calendar.MONTH), calendarRemind.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
//  create task
        activityTaskListBinding.includedBottomSheet.btnCreateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String task_name = activityTaskListBinding.includedBottomSheet.edtTaskName.getText().toString().trim();
                if (TextUtils.isEmpty(task_name)){
                    activityTaskListBinding.includedBottomSheet.edtTaskName.setError("Please fill task name");
                }
                else
                {
                    Map<String, Object> map = new HashMap<>();
                    map.put("Email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    map.put("MaDS", taskListViewModel.getId());
                    map.put("TenNV", task_name);
                    map.put("QuanTrong", false);
                    map.put("TinhTrang", false);
                    map.put("GhiChu", "");
                    if (checkSetCalendarExpired)
                        map.put("NgayDenHan", new SimpleDateFormat("dd-MM-yyyy").format(calendarExpired.getTime()));
                    else
                        map.put("NgayDenHan", "null");
                    if (checkSetCalendarRemind)
                        map.put("NhacNho", new SimpleDateFormat("dd-MM-yyyy HH:mm").format(calendarRemind.getTime()));
                    else
                        map.put("NhacNho", "null");
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    firestore.collection("nhiemvu")
                            .add(map)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful())
                                    {
                                        new SweetAlertDialog(TaskListActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                                .setTitleText("Success")
                                                .setContentText("You added new task successfully")
                                                .setConfirmText("Continue")
                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                        activityTaskListBinding.includedBottomSheet.edtTaskName.setText("");
                                                        checkSetCalendarExpired = checkSetCalendarRemind = false;
                                                        sweetAlertDialog.dismissWithAnimation();
                                                    }
                                                })
                                                .show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void initBottomSheet(){
        fbtnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// Open, Close bottom sheet
                if (bottomSheetAddTask.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetAddTask.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                else
                    bottomSheetAddTask.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
// Event for bottom sheet
        bottomSheetAddTask.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    @Override
    public void onPositiveEvent(DialogFragment dialogFragment, int requestCode) {

    }

    @Override
    public void onNegativeEvent(DialogFragment dialogFragment, int requestCode) {

    }
}