package model;

public class ChiTietHoaDon {
	private int id;
	private int idhoadon;
	private int idmonan;
	private int soLuong;
	private long tongTien;

	public ChiTietHoaDon() {
	}

	public ChiTietHoaDon(int id, int idhoadon, int idmonan, int soLuong, long tongTien) {
		this.id = id;
		this.idhoadon = idhoadon;
		this.idmonan = idmonan;
		this.soLuong = soLuong;
		this.tongTien = tongTien;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdhoadon() {
		return idhoadon;
	}

	public void setIdhoadon(int idhoadon) {
		this.idhoadon = idhoadon;
	}

	public int getIdmonan() {
		return idmonan;
	}

	public void setIdmonan(int idmonan) {
		this.idmonan = idmonan;
	}

	public int getSoLuong() {
		return soLuong;
	}

	public void setSoLuong(int soLuong) {
		this.soLuong = soLuong;
	}

	public long getTongTien() {
		return tongTien;
	}

	public void setTongTien(long tongTien) {
		this.tongTien = tongTien;
	}

}
