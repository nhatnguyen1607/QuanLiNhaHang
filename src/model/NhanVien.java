package model;

public class NhanVien {
	private int id_nhanvien;
	private String ten;
	private String sdt;
	private String diachi;
	private int id_taikhoan;
	

	public NhanVien() {}
	public int getId_nhanvien() {
		return id_nhanvien;
	}
	public void setId_nhanvien(int id_nhanvien) {
		this.id_nhanvien = id_nhanvien;
	}
	public String getTen() {
		return ten;
	}
	public void setTen(String ten) {
		this.ten = ten;
	}
	public String getSdt() {
		return sdt;
	}
	public void setSdt(String sdt) {
		this.sdt = sdt;
	}
	public String getDiachi() {
		return diachi;
	}
	public void setDiachi(String diachi) {
		this.diachi = diachi;
	}
	public int getId_taikhoan() {
		return id_taikhoan;
	}
	public void setId_taikhoan(int id_taikhoan) {
		this.id_taikhoan = id_taikhoan;
	}
	
	public NhanVien(int id_nhanvien, String ten,String sdt, String diachi, int id_taikhoan ) {
		this.id_nhanvien= id_nhanvien;
		this.ten= ten;
		this.sdt= sdt;
		this.diachi= diachi;
		this.id_taikhoan= id_taikhoan;

	}
}
