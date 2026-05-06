package org.example.quanlynhahang.models;

import java.time.LocalDateTime;

public class Order {
    private String maOrder;
    private String maHoaDon;
    private LocalDateTime thoiGianTao;
    private String trangThai; // MOI_TAO, DANG_XU_LY, HOAN_THANH

    public Order() {}

    public Order(String maOrder, String maHoaDon, LocalDateTime thoiGianTao, String trangThai) {
        this.maOrder = maOrder;
        this.maHoaDon = maHoaDon;
        this.thoiGianTao = thoiGianTao;
        this.trangThai = trangThai;
    }

    // Getter và Setter
    public String getMaOrder() { return maOrder; }
    public void setMaOrder(String maOrder) { this.maOrder = maOrder; }

    public String getMaHoaDon() { return maHoaDon; }
    public void setMaHoaDon(String maHoaDon) { this.maHoaDon = maHoaDon; }

    public LocalDateTime getThoiGianTao() { return thoiGianTao; }
    public void setThoiGianTao(LocalDateTime thoiGianTao) { this.thoiGianTao = thoiGianTao; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}