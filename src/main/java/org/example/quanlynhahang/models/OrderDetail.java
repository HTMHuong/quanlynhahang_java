package org.example.quanlynhahang.models;

import java.time.LocalDateTime;

public class OrderDetail {
    private String maOrder;
    private String maMon;
    private int soLuong;
    private double donGia;
    private String ghiChu;
    private String trangThaiMon; // MOI_DAT, DANG_CHE_BIEN, DA_XONG, DA_GIAO
    private LocalDateTime thoiGianDat;

    public OrderDetail() {}

    public OrderDetail(String maOrder, String maMon, int soLuong, double donGia, String ghiChu, String trangThaiMon, LocalDateTime thoiGianDat) {
        this.maOrder = maOrder;
        this.maMon = maMon;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.ghiChu = ghiChu;
        this.trangThaiMon = trangThaiMon;
        this.thoiGianDat = thoiGianDat;
    }

    // Getter và Setter
    public String getMaOrder() { return maOrder; }
    public void setMaOrder(String maOrder) { this.maOrder = maOrder; }

    public String getMaMon() { return maMon; }
    public void setMaMon(String maMon) { this.maMon = maMon; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

    public double getDonGia() { return donGia; }
    public void setDonGia(double donGia) { this.donGia = donGia; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public String getTrangThaiMon() { return trangThaiMon; }
    public void setTrangThaiMon(String trangThaiMon) { this.trangThaiMon = trangThaiMon; }

    public LocalDateTime getThoiGianDat() { return thoiGianDat; }
    public void setThoiGianDat(LocalDateTime thoiGianDat) { this.thoiGianDat = thoiGianDat; }
}