package org.example.quanlynhahang.models;

public class TheThanhVien {
    private String maThe;
    private String tenKhach;
    private String soDienThoai;
    private int diemTichLuy;

    public TheThanhVien(String maThe, String tenKhach, String soDienThoai, int diemTichLuy) {
        this.maThe = maThe;
        this.tenKhach = tenKhach;
        this.soDienThoai = soDienThoai;
        this.diemTichLuy = diemTichLuy;
    }

    // Getter và Setter để Java có thể đọc/ghi dữ liệu
    public String getMaThe() { return maThe; }
    public void setMaThe(String maThe) { this.maThe = maThe; }

    public String getTenKhach() { return tenKhach; }
    public void setTenKhach(String tenKhach) { this.tenKhach = tenKhach; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public int getDiemTichLuy() { return diemTichLuy; }
    public void setDiemTichLuy(int diemTichLuy) { this.diemTichLuy = diemTichLuy; }
}