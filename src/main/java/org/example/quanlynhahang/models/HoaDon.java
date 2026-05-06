package org.example.quanlynhahang.models;

import java.sql.Timestamp;

public class HoaDon {
    private String maHoaDon;
    private Timestamp ngayTao;
    private double tongTien;
    private int trangThaiThanhToan; // 0: Chưa, 1: Đang chờ (Khách gọi), 2: Đã thanh toán (Thu ngân xác nhận)
    private int diemThuong;         // Thêm cái này để quản lý điểm tích lũy cho khách
    private int soBan;
    private String maThe;

    public HoaDon(String maHoaDon, Timestamp ngayTao, double tongTien, int trangThaiThanhToan, int diemThuong, int soBan, String maThe) {
        this.maHoaDon = maHoaDon;
        this.ngayTao = ngayTao;
        this.tongTien = tongTien;
        this.trangThaiThanhToan = trangThaiThanhToan;
        this.diemThuong = diemThuong;
        this.soBan = soBan;
        this.maThe = maThe;
    }

    // Getter and Setter
    public String getMaHoaDon() { return maHoaDon; }
    public void setMaHoaDon(String maHoaDon) { this.maHoaDon = maHoaDon; }

    public Timestamp getNgayTao() { return ngayTao; }
    public void setNgayTao(Timestamp ngayTao) { this.ngayTao = ngayTao; }

    public double getTongTien() { return tongTien; }
    public void setTongTien(double tongTien) { this.tongTien = tongTien; }

    public int getTrangThaiThanhToan() { return trangThaiThanhToan; }
    public void setTrangThaiThanhToan(int trangThaiThanhToan) { this.trangThaiThanhToan = trangThaiThanhToan; }

    public int getDiemThuong() { return diemThuong; }
    public void setDiemThuong(int diemThuong) { this.diemThuong = diemThuong; }

    public int getSoBan() { return soBan; }
    public void setSoBan(int soBan) { this.soBan = soBan; }

    public String getMaThe() { return maThe; }
    public void setMaThe(String maThe) { this.maThe = maThe; }
}