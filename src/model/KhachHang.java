package model;

import java.time.LocalDate;

public class KhachHang {
    private int id;
    private String ten; 
    private String soDienThoai;
    private int diemTichLuy;
    private String capBac;
    private LocalDate sinhNhat;
    private String email;


    public KhachHang() {}

    public KhachHang(int id, String ten, String soDienThoai, int diemTichLuy, String capBac, LocalDate sinhNhat, String email) {
        this.id = id;
        this.ten = ten;
        this.soDienThoai = soDienThoai;
        this.diemTichLuy = diemTichLuy;
        this.capBac = capBac;
        this.sinhNhat = sinhNhat;
        this.email = email;
    }

    public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public LocalDate getSinhNhat() {
        return sinhNhat;
    }

    public void setSinhNhat(LocalDate sinhNhat) {
        this.sinhNhat = sinhNhat;
    }

    public int getDiemTichLuy() {
        return diemTichLuy;
    }

    public void setDiemTichLuy(int diemTichLuy) {
        this.diemTichLuy = diemTichLuy;
    }

    public String getCapBac() {
        return capBac;
    }

    public void setCapBac(String capBac) {
        this.capBac = capBac;
    }
}