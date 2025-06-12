package model;

public class MonAn {
    private int id;
    private String tenMon;
    private int gia;
    private String trangThai;
    private String moTa;
    private String hinhAnh;
    private int id_thumuc;
    public MonAn() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTenMon() {
        return tenMon;
    }

    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
    }

    public int getGia() {
        return gia;
    }

    public void setGia(int gia) {
        this.gia = gia;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public int getId_thumuc() {
		return id_thumuc;
	}

	public void setId_thumuc(int id_thumuc) {
		this.id_thumuc = id_thumuc;
	}

	public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public MonAn(int id, String tenMon, int gia, String trangThai, String moTa, String hinhAnh, int id_thumuc) {
        this.id = id;
        this.tenMon = tenMon;
        this.gia = gia;
        this.trangThai = trangThai;
        this.moTa = moTa;
        this.hinhAnh = hinhAnh;
        this.id_thumuc = id_thumuc;
    }

    // Getter, Setter
}