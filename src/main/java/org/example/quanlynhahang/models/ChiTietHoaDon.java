package org.example.quanlynhahang.models;

public class ChiTietHoaDon {
    private String maHoaDon;  // Thêm để biết thuộc hóa đơn nào
    private String maMon;     // Thêm để link với bảng MONAN
    private String tenMon;
    private int soLuong;
    private double donGia;    // Thêm giá tại thời điểm bán
    private String ghiChu;
    private String trangThaiMon;
    private String soBan;     // Giữ lại để hiển thị cho Bếp dễ nhìn

    // Constructor đầy đủ để dùng khi SELECT từ Database lên
    public ChiTietHoaDon(String maHoaDon, String maMon, String tenMon, int soLuong, double donGia, String ghiChu, String trangThaiMon, String soBan) {
        this.maHoaDon = maHoaDon;
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.ghiChu = ghiChu;
        this.trangThaiMon = trangThaiMon;
        this.soBan = soBan;
    }

    // Getter và Setter (Bắt buộc phải có để TableView hiển thị được dữ liệu)
    public String getMaHoaDon() { return maHoaDon; }
    public void setMaHoaDon(String maHoaDon) { this.maHoaDon = maHoaDon; }

    public String getMaMon() { return maMon; }
    public void setMaMon(String maMon) { this.maMon = maMon; }

    public String getTenMon() { return tenMon; }
    public void setTenMon(String tenMon) { this.tenMon = tenMon; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

    public double getDonGia() { return donGia; }
    public void setDonGia(double donGia) { this.donGia = donGia; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public String getTrangThaiMon() { return trangThaiMon; }
    public void setTrangThaiMon(String trangThaiMon) { this.trangThaiMon = trangThaiMon; }

    public String getSoBan() { return soBan; }
    public void setSoBan(String soBan) { this.soBan = soBan; }
}