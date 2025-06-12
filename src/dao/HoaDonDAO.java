package dao;

import model.HoaDon;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO {
    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    public int addHoaDon(HoaDon hoaDon) throws SQLException {
        String sql = "INSERT INTO hoadon (orderId, tongTien, thoiGianThanhToan, id_khachhang, phuongThucThanhToan) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, hoaDon.getOrderId());
            pstmt.setLong(2, hoaDon.getTongTien());
            pstmt.setTimestamp(3, Timestamp.valueOf(hoaDon.getThoiGianThanhToan()));
            pstmt.setInt(4, hoaDon.getId_khachhang());
            pstmt.setString(5, hoaDon.getPhuongThucThanhToan());
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public HoaDon getHoaDonById(int id) throws SQLException {
        String sql = "SELECT * FROM hoadon WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return new HoaDon(
                    rs.getInt("id"),
                    rs.getInt("orderId"),
                    rs.getLong("tongTien"),
                    rs.getTimestamp("thoiGianThanhToan").toLocalDateTime(),
                    rs.getInt("id_khachhang"),
                    rs.getString("phuongThucThanhToan")
                );
            }
        }
        return null;
    }

    public HoaDon getHoaDonByOrderId(int id) throws SQLException {
        String sql = "SELECT * FROM hoadon WHERE orderId = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id); 
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new HoaDon(
                        rs.getInt("id"),
                        rs.getInt("orderId"),
                        rs.getLong("tongTien"),
                        rs.getTimestamp("thoiGianThanhToan").toLocalDateTime(),
                        rs.getInt("id_khachhang"),
                        rs.getString("phuongThucThanhToan")
                    );
                }
            }
        }
        return null;
    }


    public List<HoaDon> getAllHoaDon() throws SQLException {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM hoadon";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new HoaDon(
                    rs.getInt("id"),
                    rs.getInt("orderId"),
                    rs.getLong("tongTien"),
                    rs.getTimestamp("thoiGianThanhToan").toLocalDateTime(),
                    rs.getInt("id_khachhang"),
                    rs.getString("phuongThucThanhToan")
                ));
            }
        }
        return list;
    }
}