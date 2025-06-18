package dao;

import model.ChiTietOrder;
import utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChiTietOrderDAO {
    public void addChiTietOrder(ChiTietOrder chiTiet) throws SQLException {
        String query = "INSERT INTO ChiTietOrder (orderId, monAnId, soLuong, trangThai) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, chiTiet.getOrderId());
            stmt.setInt(2, chiTiet.getMonAnId());
            stmt.setInt(3, chiTiet.getSoLuong());
            stmt.setString(4, chiTiet.getTrangThai());
            stmt.executeUpdate();
        }
    }

    public void updateChiTietOrderStatus(int orderId, String newStatus) throws SQLException {
        String query = "UPDATE ChiTietOrder SET trangThai = ? WHERE orderId = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, orderId);
            stmt.executeUpdate();
        }
    }

    public List<ChiTietOrder> getChiTietOrdersByOrderId(int orderId) throws SQLException {
        List<ChiTietOrder> chiTietList = new ArrayList<>();
        String query = "SELECT * FROM ChiTietOrder WHERE orderId = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ChiTietOrder chiTiet = new ChiTietOrder();
                    chiTiet.setId(rs.getInt("id"));
                    chiTiet.setOrderId(rs.getInt("orderId"));
                    chiTiet.setMonAnId(rs.getInt("monAnId"));
                    chiTiet.setSoLuong(rs.getInt("soLuong"));
                    chiTiet.setTrangThai(rs.getString("trangThai"));
                    chiTietList.add(chiTiet);
                }
            }
        }
        return chiTietList;
    }

    public List<ChiTietOrder> getChiTietOrdersByOrderIdAndMonAnId(int orderId, int monAnId) throws SQLException {
        List<ChiTietOrder> chiTietList = new ArrayList<>();
        String query = "SELECT * FROM ChiTietOrder WHERE orderId = ? AND monAnId = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, orderId);
            stmt.setInt(2, monAnId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ChiTietOrder chiTiet = new ChiTietOrder();
                    chiTiet.setId(rs.getInt("id"));
                    chiTiet.setOrderId(rs.getInt("orderId"));
                    chiTiet.setMonAnId(rs.getInt("monAnId"));
                    chiTiet.setSoLuong(rs.getInt("soLuong"));
                    chiTiet.setTrangThai(rs.getString("trangThai"));
                    chiTietList.add(chiTiet);
                }
            }
        }
        return chiTietList;
    }

    public String getChiTietOrderStatus(int orderId, int monAnId) throws SQLException {
        String query = "SELECT trangThai FROM ChiTietOrder WHERE orderId = ? AND monAnId = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, orderId);
            stmt.setInt(2, monAnId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("trangThai");
                }
            }
        }
        return "Moi"; // Mặc định nếu không tìm thấy
    }

    public void deleteAllChiTietOrder(int orderId) throws SQLException {
        String query = "DELETE FROM ChiTietOrder WHERE orderId = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, orderId);
            stmt.executeUpdate();
        }
    }
}