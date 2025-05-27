package dao;

import model.ThuMucMonAn;
import utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ThuMucMonAnDAO {
    public List<ThuMucMonAn> getAllThuMucMonAn() {
        List<ThuMucMonAn> thuMucList = new ArrayList<>();
        String query = "SELECT * FROM ThuMucMonAn";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ThuMucMonAn thuMuc = new ThuMucMonAn();
                thuMuc.setId_thumuc(rs.getInt("id_thumuc"));
                thuMuc.setTen_thumuc(rs.getString("ten_thu_muc"));
                thuMucList.add(thuMuc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return thuMucList;
    }
}