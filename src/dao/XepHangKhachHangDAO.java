package dao;

import model.XepHangKhachHang;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class XepHangKhachHangDAO {
    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    public XepHangKhachHang getXepHangByDiem(int diemTichLuy) throws SQLException {
        String sql = "SELECT * FROM xephangkhachhang WHERE dieuKienDiem <= ? ORDER BY dieuKienDiem DESC LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, diemTichLuy);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new XepHangKhachHang(
                        rs.getInt("id"),
                        rs.getString("capBac"),
                        rs.getInt("dieuKienDiem"),
                        rs.getFloat("uuDai")
                    );
                }
            }
        }
        return null; // Trả về null nếu không tìm thấy cấp bậc phù hợp
    }

    public List<XepHangKhachHang> getAllXepHang() throws SQLException {
        List<XepHangKhachHang> list = new ArrayList<>();
        String sql = "SELECT * FROM xephangkhachhang";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new XepHangKhachHang(
                    rs.getInt("id"),
                    rs.getString("capBac"),
                    rs.getInt("dieuKienDiem"),
                    rs.getFloat("uuDai")
                ));
            }
        }
        return list;
    }
}