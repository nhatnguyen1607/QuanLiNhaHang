package model;

public class ChiTietOrder {
    private int id;
    private int orderId;
    private int monAnId;
    private int soLuong;
    private String trangThai;

    public ChiTietOrder() {}

    public ChiTietOrder(int id, int orderId, int monAnId, int soLuong, String trangThai) {
        this.id = id;
        this.orderId = orderId;
        this.monAnId = monAnId;
        this.soLuong = soLuong;
        this.trangThai = trangThai;
    }

    // Getter, Setter


    public String getTrangThai() {
		return trangThai;
	}

	public void setTrangThai(String trangThai) {
		this.trangThai = trangThai;
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

    public int getMonAnId() {
        return monAnId;
    }

    public void setMonAnId(int monAnId) {
        this.monAnId = monAnId;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

}
