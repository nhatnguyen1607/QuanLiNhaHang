package dao;

import model.MonAn;
import utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MonAnDAO {
    public List<MonAn> getMonAnByThuMuc(int id_thumuc) {
        List<MonAn> monAnList = new ArrayList<>();
        String query = "SELECT * FROM MonAn WHERE id_thumuc = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id_thumuc);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MonAn monAn = new MonAn();
                    monAn.setId(rs.getInt("id"));
                    monAn.setTenMon(rs.getString("tenMon"));
                    monAn.setGia(rs.getInt("gia"));
                    monAn.setTrangThai(rs.getString("trangThai"));
                    monAn.setMoTa(rs.getString("moTa"));
                    monAn.setHinhAnh(rs.getString("hinhAnh"));
                    monAn.setId_thumuc(rs.getInt("id_thumuc"));
                    monAnList.add(monAn);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return monAnList;
    }

    public MonAn getMonAnById(int id) {
        MonAn monAn = null;
        String query = "SELECT * FROM MonAn WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    monAn = new MonAn();
                    monAn.setId(rs.getInt("id"));
                    monAn.setTenMon(rs.getString("tenMon"));
                    monAn.setGia(rs.getInt("gia"));
                    monAn.setTrangThai(rs.getString("trangThai"));
                    monAn.setMoTa(rs.getString("moTa"));
                    monAn.setHinhAnh(rs.getString("hinhAnh"));
                    monAn.setId_thumuc(rs.getInt("id_thumuc"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return monAn;
    }

    public void updateMonAnTrangThai(int monAnId, String trangThai) throws SQLException {
        String sql = "UPDATE MonAn SET trangThai = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, trangThai);
            stmt.setInt(2, monAnId);
            stmt.executeUpdate();
        }
    }
}