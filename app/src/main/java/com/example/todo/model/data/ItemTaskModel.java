package com.example.todo.model.data;

public class ItemTaskModel {
    int icon;
    String title;
    String id;
    int stt;
    public ItemTaskModel(int icon, String title) {
        this.icon = icon;
        this.title = title;
    }
    public ItemTaskModel(int icon, String title, String id,  int stt) {
        this.icon = icon;
        this.title = title;
        this.stt = stt;
        this.id = id;
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
}
