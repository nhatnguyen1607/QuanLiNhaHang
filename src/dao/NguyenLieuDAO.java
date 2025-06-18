package dao;

import model.NguyenLieu;
import utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NguyenLieuDAO {
    public NguyenLieu getNguyenLieuById(int nguyenLieuId) throws SQLException {
        String sql = "SELECT * FROM nguyenlieu WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, nguyenLieuId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new NguyenLieu(
                        rs.getInt("id"),
                        rs.getString("tenNguyenLieu"),
                        rs.getString("donViTinh"),
                        rs.getDouble("soLuongTon"),
                        rs.getInt("giaNhap")
                    );
                }
            }
        }
        return null;
    }

    public void updateSoLuongTon(int nguyenLieuId, double newSoLuongTon) throws SQLException {
        String sql = "UPDATE nguyenlieu SET soLuongTon = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, newSoLuongTon);
            stmt.setInt(2, nguyenLieuId);
            stmt.executeUpdate();
        }
    }
}