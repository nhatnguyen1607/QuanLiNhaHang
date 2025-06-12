package dao;

import model.NhanVien;
import utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NhanVienDAO {
    public NhanVien getNhanVienByIdTaiKhoan(int idTaiKhoan) throws SQLException {
        String query = "SELECT id_nhanvien, ten, sdt, diachi, id_taikhoan FROM nhanvien WHERE id_taikhoan = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idTaiKhoan);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    NhanVien nhanVien = new NhanVien();
                    nhanVien.setId_nhanvien(rs.getInt("id_nhanvien"));
                    nhanVien.setTen(rs.getString("ten"));
                    nhanVien.setSdt(rs.getString("sdt"));
                    nhanVien.setDiachi(rs.getString("diachi"));
                    nhanVien.setId_taikhoan(rs.getInt("id_taikhoan"));
                    return nhanVien;
                }
            }
        }
        return null;
    }
}