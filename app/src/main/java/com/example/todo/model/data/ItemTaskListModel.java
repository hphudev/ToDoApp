package com.example.todo.model.data;

import android.graphics.Color;

public class ItemTaskListModel {
    int icon;
    String title;
    String id;
    int stt;
    int backgroundColor;
    int textColor;
    public ItemTaskListModel(int icon, String title) {
        this.icon = icon;
        this.title = title;
        id = "null";
        stt = 0;
        backgroundColor = Color.BLUE;
        textColor = Color.WHITE;
    }
    public ItemTaskListModel(int icon, String title, String id, int stt, int backgroundColor, int textColor) {
        this.icon = icon;
        this.title = title;
        this.stt = stt;
        this.id = id;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStt() {
        return stt;
    }

    public void setStt(int stt) {
        this.stt = stt;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }
}
