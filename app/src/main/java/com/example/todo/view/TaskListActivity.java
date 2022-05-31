package com.example.todo.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.example.todo.R;
import com.example.todo.databinding.ActivityTaskListBinding;
import com.example.todo.viewmodel.TaskListViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TaskListActivity extends AppCompatActivity {

    private ActivityTaskListBinding activityTaskListBinding;
    private TaskListViewModel taskListViewModel;
    private Toolbar toolbar;
    private BottomSheetBehavior bottomSheetAddTask;
    private FloatingActionButton fbtnAddTask;
    private LinearLayout layoutBottomSheetAddTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

//        getSupportActionBar().hide();
        activityTaskListBinding = DataBindingUtil.setContentView(this, R.layout.activity_task_list);
        taskListViewModel = new TaskListViewModel(this);
        activityTaskListBinding.setTaskListViewModel(taskListViewModel);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        taskListViewModel.setTitle(bundle.getString("title"));
        taskListViewModel.setId(bundle.getString("id"));
        String hexColor = String.format("#%06X", (0xFFFFFF & bundle.getInt("backgroundColor")));
        taskListViewModel.setBackgroundColor(Color.parseColor(hexColor));
        hexColor = String.format("#%06X", (0xFFFFFF & bundle.getInt("titleColor")));
        taskListViewModel.setTitleColor(Color.parseColor(hexColor));
        fbtnAddTask = (FloatingActionButton)findViewById(R.id.fbtn_add_task);
        layoutBottomSheetAddTask = (LinearLayout)findViewById(R.id.layout_bottom_sheet_add_task);
        bottomSheetAddTask = BottomSheetBehavior.from(layoutBottomSheetAddTask);
        init();
    }

    private void init(){
        fbtnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Đã vào", Toast.LENGTH_LONG).show();
                if (bottomSheetAddTask.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetAddTask.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                else
                    bottomSheetAddTask.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        bottomSheetAddTask.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }
}