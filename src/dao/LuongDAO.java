package dao;

import java.sql.*;

import utils.DatabaseConnection;

public class LuongDAO {
    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    public void updateDiemThuong(int idNhanVien, int diemThuong) throws SQLException {
        String sql = "UPDATE luong SET diemthuong = ? WHERE id_nhanvien = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, diemThuong);
            pstmt.setInt(2, idNhanVien);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                // Nếu không tìm thấy id_nhanvien, có thể thêm logic chèn mới
                String insertSql = "INSERT INTO luong (id_nhanvien, diemthuong) VALUES (?, ?)";
                try (PreparedStatement insertPstmt = conn.prepareStatement(insertSql)) {
                    insertPstmt.setInt(1, idNhanVien);
                    insertPstmt.setInt(2, diemThuong);
                    insertPstmt.executeUpdate();
                }
            }
        }
    }

    public int getDiemThuongByIdNhanVien(int idNhanVien) throws SQLException {
        String sql = "SELECT diemthuong FROM luong WHERE id_nhanvien = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idNhanVien); // Gán giá trị cho tham số
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("diemthuong");
                }
            }
        }
        return 0; // Trả về 0 nếu không tìm thấy
    }
}
