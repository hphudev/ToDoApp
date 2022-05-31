package com.example.todo.viewmodel;

import android.app.Activity;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.example.todo.BR;

public class TaskListViewModel extends BaseObservable {
    private String title;
    private String id;
    private int backgroundColor;
    private int titleColor;
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

    @Bindable
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @Bindable
    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        notifyPropertyChanged(BR.backgroundColor);
    }

    @Bindable
    public int getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
        notifyPropertyChanged(BR.titleColor);
    }
}
