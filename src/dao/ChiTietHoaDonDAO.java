package dao;

import model.ChiTietHoaDon;
import utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChiTietHoaDonDAO {
    // Không cần biến conn thành viên, vì mỗi phương thức sẽ tự quản lý Connection

    public void addChiTietHoaDon(ChiTietHoaDon chiTiet) throws SQLException {
        String sql = "INSERT INTO chitiethoadon (idhoadon, idmonan, soluong, tongtien) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, chiTiet.getIdhoadon());
            stmt.setInt(2, chiTiet.getIdmonan());
            stmt.setInt(3, chiTiet.getSoLuong());
            stmt.setLong(4, chiTiet.getTongTien());
            stmt.executeUpdate();
        }
    }

    public List<ChiTietHoaDon> getChiTietHoaDonByHoaDonId(int idHoaDon) throws SQLException {
        List<ChiTietHoaDon> chiTietList = new ArrayList<>();
        String sql = "SELECT * FROM chitiethoadon WHERE idhoadon = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idHoaDon);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ChiTietHoaDon chiTiet = new ChiTietHoaDon(
                        rs.getInt("id"),
                        rs.getInt("idhoadon"),
                        rs.getInt("idmonan"),
                        rs.getInt("soluong"),
                        rs.getLong("tongtien")
                    );
                    chiTietList.add(chiTiet);
                }
            }
        }
        return chiTietList;
    }
}