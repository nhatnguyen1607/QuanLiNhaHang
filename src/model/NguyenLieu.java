package model;

public class NguyenLieu {
    private int id;
    private String tenNguyenLieu;
    private String donViTinh;
    private int soLuongTon;


    public int getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(int soLuongTon) {
        this.soLuongTon = soLuongTon;
    }

    public String getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(String donViTinh) {
        this.donViTinh = donViTinh;
    }

    public String getTenNguyenLieu() {
        return tenNguyenLieu;
    }

    public void setTenNguyenLieu(String tenNguyenLieu) {
        this.tenNguyenLieu = tenNguyenLieu;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public NguyenLieu() {}

    public NguyenLieu(int id, String tenNguyenLieu, String donViTinh, int soLuongTon) {
        this.id = id;
        this.tenNguyenLieu = tenNguyenLieu;
        this.donViTinh = donViTinh;
        this.soLuongTon = soLuongTon;
    }

    // Getter, Setter
}
