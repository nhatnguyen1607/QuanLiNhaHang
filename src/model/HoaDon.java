package model;

import java.time.LocalDateTime;

public class HoaDon {
    private int id;
    private int orderId;
    private long tongTien;
    private LocalDateTime thoiGianThanhToan;
    private int id_khachhang;
    private String phuongThucThanhToan;

    public HoaDon() {}

    public HoaDon(int id, int orderId, long tongTien, LocalDateTime thoiGianThanhToan, int id_khachhang, String phuongThucThanhToan) {
        this.id = id;
        this.orderId = orderId;
        this.tongTien = tongTien;
        this.thoiGianThanhToan = thoiGianThanhToan;
        this.id_khachhang = id_khachhang;
        this.phuongThucThanhToan = phuongThucThanhToan;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public long getTongTien() {
        return tongTien;
    }

    public void setTongTien(long tongTien) {
        this.tongTien = tongTien;
    }

    public LocalDateTime getThoiGianThanhToan() {
        return thoiGianThanhToan;
    }

    public void setThoiGianThanhToan(LocalDateTime thoiGianThanhToan) {
        this.thoiGianThanhToan = thoiGianThanhToan;
    }

    public int getId_khachhang() {
        return id_khachhang;
    }

    public void setId_khachhang(int id_khachhang) {
        this.id_khachhang = id_khachhang;
    }

    public String getPhuongThucThanhToan() {
        return phuongThucThanhToan;
    }

    public void setPhuongThucThanhToan(String phuongThucThanhToan) {
        this.phuongThucThanhToan = phuongThucThanhToan;
    }
}