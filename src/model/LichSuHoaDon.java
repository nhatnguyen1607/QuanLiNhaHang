package model;

import java.time.LocalDate;

public class LichSuHoaDon {
    private int id;
    private int khachHangId;
    private double tongTien;
    private LocalDate ngayThanhToan;

    public LichSuHoaDon() {}

    public LichSuHoaDon(int id, int khachHangId, double tongTien, LocalDate ngayThanhToan) {
        this.id = id;
        this.khachHangId = khachHangId;
        this.tongTien = tongTien;
        this.ngayThanhToan = ngayThanhToan;
    }

    // Getter, Setter

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getKhachHangId() {
        return khachHangId;
    }

    public void setKhachHangId(int khachHangId) {
        this.khachHangId = khachHangId;
    }

    public double getTongTien() {
        return tongTien;
    }

    public void setTongTien(double tongTien) {
        this.tongTien = tongTien;
    }

    public LocalDate getNgayThanhToan() {
        return ngayThanhToan;
    }

    public void setNgayThanhToan(LocalDate ngayThanhToan) {
        this.ngayThanhToan = ngayThanhToan;
    }
}
