package dao;

import model.TaiKhoan;
import utils.DatabaseConnection;
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
}