package dao;

import model.MonAnNguyenLieu;
import utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MonAnNguyenLieuDAO {
    public List<MonAnNguyenLieu> getMonAnNguyenLieuByMonAnId(int monAnId) throws SQLException {
        List<MonAnNguyenLieu> list = new ArrayList<>();
        String sql = "SELECT * FROM monan_nguyenlieu WHERE monAnId = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, monAnId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MonAnNguyenLieu item = new MonAnNguyenLieu(
                        rs.getInt("id"),
                        rs.getInt("monAnId"),
                        rs.getInt("nguyenLieuId"),
                        rs.getDouble("soLuongCan")
                    );
                    list.add(item);
                }
            }
        }
        return list;
    }
}