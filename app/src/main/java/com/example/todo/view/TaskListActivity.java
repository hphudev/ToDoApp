package com.example.todo.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.todo.R;

public class TaskListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        getSupportActionBar().hide();
    }
}