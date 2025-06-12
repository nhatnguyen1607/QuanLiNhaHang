package model;

public class GioHangItem {
    private MonAn monAn;
    private int soLuong;
    private String trangThai; 

    public GioHangItem(MonAn monAn, int soLuong) {
        this.monAn = monAn;
        this.soLuong = soLuong;
        this.trangThai = "Moi"; 
    }

    public GioHangItem(MonAn monAn, int soLuong, String trangThai) {
        this.monAn = monAn;
        this.soLuong = soLuong;
        this.trangThai = trangThai; 
    }

    public MonAn getMonAn() {
        return monAn;
    }

    public void setMonAn(MonAn monAn) {
        this.monAn = monAn;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}