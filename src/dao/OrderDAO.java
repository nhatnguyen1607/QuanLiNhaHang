package dao;

import model.Order;
import utils.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    public int addOrder(Order order) throws SQLException {
        String sql = "INSERT INTO `order` (banAnId, id_nhanvien, thoiGianTao, trangThai, ghiChu) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, order.getBanAnId());
            pstmt.setInt(2, order.getId_nhanvien());
            pstmt.setObject(3, order.getThoiGianTao());
            pstmt.setString(4, order.getTrangThai());
            pstmt.setString(5, order.getGhiChu());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public List<Order> getOrdersByBanAnId(int banAnId) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM `order` WHERE banAnId = ? AND trangThai IN ('DangDat', 'DaXong', 'DaThanhToan')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, banAnId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order(
                        rs.getInt("id"),
                        rs.getInt("banAnId"),
                        rs.getInt("id_nhanvien"),
                        rs.getObject("thoiGianTao", LocalDateTime.class),
                        rs.getString("trangThai"),
                        rs.getString("ghiChu")
                    );
                    orders.add(order);
                }
            }
        }
        return orders;
    }

    public void updateOrderStatus(int orderId, String trangThai) throws SQLException {
        String sql = "UPDATE `order` SET trangThai = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, trangThai);
            pstmt.setInt(2, orderId);
            pstmt.executeUpdate();
        }
    }

    public void updateOrderGhiChu(int orderId, String ghiChu) throws SQLException {
        String sql = "UPDATE `order` SET ghiChu = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ghiChu);
            pstmt.setInt(2, orderId);
            pstmt.executeUpdate();
        }
    }
}