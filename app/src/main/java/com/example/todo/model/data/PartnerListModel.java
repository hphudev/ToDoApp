package com.example.todo.model.data;

public class PartnerListModel {
    private String MaDS;
    private String Email;

    public PartnerListModel(String maDS, String email) {
        MaDS = maDS;
        Email = email;
    }

    public String getMaDS() {
        return MaDS;
    }

    public void setMaDS(String maDS) {
        MaDS = maDS;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
