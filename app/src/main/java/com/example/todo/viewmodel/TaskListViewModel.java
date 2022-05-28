package com.example.todo.viewmodel;

import android.app.Activity;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.example.todo.BR;

public class TaskListViewModel extends BaseObservable {
    private String title;
    private Activity activity;

    public TaskListViewModel(Activity activity) {
        this.activity = activity;
    }

    @Bindable
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        notifyPropertyChanged(BR.title);
    }
}
