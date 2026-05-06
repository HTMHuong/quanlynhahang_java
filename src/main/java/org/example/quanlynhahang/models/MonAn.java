package org.example.quanlynhahang.models;

public class MonAn {
    private String maMon;
    private String tenMon;
    private double giaTien;
    private String moTa;
    private String hinhAnh;
    private String danhMuc;
    private boolean isAvailable;

    // Thuộc tính phục vụ giỏ hàng và hiển thị TableView
    private int soLuong;
    private String ghiChu;

    // Constructor dùng khi load từ Database
    public MonAn(String maMon, String tenMon, double giaTien, String moTa, String hinhAnh, String danhMuc, boolean isAvailable) {
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.giaTien = giaTien;
        this.moTa = moTa;
        this.hinhAnh = hinhAnh;
        this.danhMuc = danhMuc;
        this.isAvailable = isAvailable;
        this.soLuong = 1;
        this.ghiChu = "";
    }

    // --- GETTER & SETTER ---
    // TableView sẽ gọi các hàm get... này để hiển thị dữ liệu lên các cột

    public String getMaMon() { return maMon; }
    public void setMaMon(String maMon) { this.maMon = maMon; }

    public String getTenMon() { return tenMon; }
    public void setTenMon(String tenMon) { this.tenMon = tenMon; }

    public double getGiaTien() { return giaTien; }
    public void setGiaTien(double giaTien) { this.giaTien = giaTien; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }

    public String getDanhMuc() { return danhMuc; }
    public void setDanhMuc(String danhMuc) { this.danhMuc = danhMuc; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    // Tính thành tiền (Giá tiền * Số lượng)
    public double getThanhTien() {
        return this.giaTien * this.soLuong;
    }
}