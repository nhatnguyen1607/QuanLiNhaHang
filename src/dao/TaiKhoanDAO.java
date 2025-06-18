package dao;

import model.TaiKhoan;
import utils.DatabaseConnection;
import utils.HashUtil; // Import HashUtil để mã hóa nếu cần
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TaiKhoanDAO {
    public TaiKhoan getTaiKhoanByEmailAndMatKhau(String email, String matKhau) throws SQLException {
        String query = "SELECT * FROM taikhoan WHERE email = ? AND matKhau = ? AND vaiTro = 'nhanvien' AND trangThai = 'mo'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, matKhau);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    TaiKhoan taiKhoan = new TaiKhoan();
                    taiKhoan.setId_taikhoan(rs.getInt("id_taikhoan"));
                    taiKhoan.setEmail(rs.getString("email"));
                    taiKhoan.setMatKhau(rs.getString("matKhau"));
                    taiKhoan.setVaiTro(rs.getString("vaiTro"));
                    taiKhoan.setTrangThai(rs.getString("trangThai"));
                    return taiKhoan;
                }
            }
        }
        return null;
    }

    // Lấy mật khẩu hiện tại của tài khoản dựa trên id_taikhoan
    public String getMatKhau(int idTaiKhoan) throws SQLException {
        String query = "SELECT matKhau FROM taikhoan WHERE id_taikhoan = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idTaiKhoan);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("matKhau");
                }
            }
        }
        return null;
    }

    // Cập nhật mật khẩu mới cho tài khoản
    public boolean updateMatKhau(int idTaiKhoan, String newMatKhau) throws SQLException {
        String query = "UPDATE taikhoan SET matKhau = ? WHERE id_taikhoan = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newMatKhau); // Giả sử newMatKhau đã được mã hóa bằng SHA-256
            stmt.setInt(2, idTaiKhoan);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
}