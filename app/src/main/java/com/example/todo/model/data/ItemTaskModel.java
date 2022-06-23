package com.example.todo.model.data;

public class ItemTaskModel {
    private int idInGroup;
    private String id;
    private String MaDS;
    private String TenNV;
    private String NgayDenHan;
    private String NhacNho;
    private Boolean QuanTrong;
    private Boolean TinhTrang;

    public ItemTaskModel(int idInGroup, String id, String maDS, String tenNV, String ngayDenHan, String nhacNho, Boolean quanTrong, Boolean tinhTrang) {
        this.idInGroup = idInGroup;
        this.id = id;
        MaDS = maDS;
        TenNV = tenNV;
        NgayDenHan = ngayDenHan;
        NhacNho = nhacNho;
        QuanTrong = quanTrong;
        TinhTrang = tinhTrang;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMaDS() {
        return MaDS;
    }

    public void setMaDS(String maDS) {
        MaDS = maDS;
    }

    public String getTenNV() {
        return TenNV;
    }

    public void setTenNV(String tenNV) {
        TenNV = tenNV;
    }

    public String getNgayDenHan() {
        return NgayDenHan;
    }

    public void setNgayDenHan(String ngayDenHan) {
        NgayDenHan = ngayDenHan;
    }

    public String getNhacNho() {
        return NhacNho;
    }

    public void setNhacNho(String nhacNho) {
        NhacNho = nhacNho;
    }

    public Boolean getQuanTrong() {
        return QuanTrong;
    }

    public void setQuanTrong(Boolean quanTrong) {
        QuanTrong = quanTrong;
    }

    public Boolean getTinhTrang() {
        return TinhTrang;
    }

    public void setTinhTrang(Boolean tinhTrang) {
        TinhTrang = tinhTrang;
    }

    public int getIdInGroup() {
        return idInGroup;
    }

    public void setIdInGroup(int idInGroup) {
        this.idInGroup = idInGroup;
    }
}
