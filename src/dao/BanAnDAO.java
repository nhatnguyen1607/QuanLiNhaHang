package dao;

import model.BanAn;
import utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BanAnDAO {
    public List<BanAn> getAllBanAn() {
        List<BanAn> banAnList = new ArrayList<>();
        String query = "SELECT * FROM BanAn";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                BanAn banAn = new BanAn();
                banAn.setId(rs.getInt("id"));
                banAn.setTrangThai(rs.getString("trangThai"));
                banAnList.add(banAn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return banAnList;
    }

    public BanAn getBanAnById(int id) {
        BanAn banAn = null;
        String query = "SELECT * FROM BanAn WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    banAn = new BanAn();
                    banAn.setId(rs.getInt("id"));
                    banAn.setTrangThai(rs.getString("trangThai"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return banAn;
    }

    public void updateBanAn(BanAn banAn) {
        String query = "UPDATE BanAn SET trangThai = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, banAn.getTrangThai());
            stmt.setInt(2, banAn.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}