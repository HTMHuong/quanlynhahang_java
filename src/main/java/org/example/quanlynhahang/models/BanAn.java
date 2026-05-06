package org.example.quanlynhahang.models;

public class BanAn {
    private int soBan;
    private String trangThai;

    public BanAn(int soBan, String trangThai) {
        this.soBan = soBan;
        this.trangThai = trangThai;
    }

    public int getSoBan() { return soBan; }
    public void setSoBan(int soBan) { this.soBan = soBan; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}