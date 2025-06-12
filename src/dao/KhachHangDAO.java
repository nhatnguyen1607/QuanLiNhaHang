package dao;

import model.KhachHang;
import model.XepHangKhachHang;
import utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class KhachHangDAO {
    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    public int addKhachHang(KhachHang khachHang) throws SQLException {
        XepHangKhachHangDAO xepHangDAO = new XepHangKhachHangDAO();
        XepHangKhachHang defaultRank = xepHangDAO.getXepHangByDiem(0);
        String defaultCapBac = (defaultRank != null) ? defaultRank.getCapBac() : "Thanh Vien";

        String sql = "INSERT INTO khachhang (hoTen, soDienThoai, diemTichLuy, capBac, sinhNhat) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, khachHang.getTen());
            pstmt.setString(2, khachHang.getSoDienThoai());
            pstmt.setInt(3, khachHang.getDiemTichLuy());
            pstmt.setString(4, defaultCapBac);
            pstmt.setDate(5, khachHang.getSinhNhat() != null ? Date.valueOf(khachHang.getSinhNhat()) : Date.valueOf(LocalDate.now()));
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public void updateKhachHang(KhachHang khachHang) throws SQLException {
        XepHangKhachHangDAO xepHangDAO = new XepHangKhachHangDAO();
        XepHangKhachHang newRank = xepHangDAO.getXepHangByDiem(khachHang.getDiemTichLuy());
        String newCapBac = (newRank != null) ? newRank.getCapBac() : "Thanh Vien";

        String sql = "UPDATE khachhang SET hoTen = ?, soDienThoai = ?, diemTichLuy =  ?, capBac = ?, sinhNhat = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, khachHang.getTen());
            pstmt.setString(2, khachHang.getSoDienThoai());
            pstmt.setInt(3, khachHang.getDiemTichLuy()); // Phần tăng thêm
            pstmt.setString(4, newCapBac);
            pstmt.setDate(5, khachHang.getSinhNhat() != null ? Date.valueOf(khachHang.getSinhNhat()) : null);
            pstmt.setInt(6, khachHang.getId());
            pstmt.executeUpdate();
        }
    }

    public KhachHang getKhachHangById(int id) throws SQLException {
        String sql = "SELECT * FROM khachhang WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new KhachHang(
                        rs.getInt("id"),
                        rs.getString("hoTen"),
                        rs.getString("soDienThoai"),
                        rs.getInt("diemTichLuy"),
                        rs.getString("capBac"),
                        rs.getDate("sinhNhat") != null ? rs.getDate("sinhNhat").toLocalDate() : null
                    );
                }
            }
        }
        return null;
    }

    public KhachHang getKhachHangBySoDienThoai(String soDienThoai) throws SQLException {
//        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
//            throw new IllegalArgumentException("Số điện thoại không được để trống!");
//        }

        String sql = "SELECT * FROM khachhang WHERE soDienThoai = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, soDienThoai);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new KhachHang(
                        rs.getInt("id"),
                        rs.getString("hoTen"),
                        rs.getString("soDienThoai"),
                        rs.getInt("diemTichLuy"),
                        rs.getString("capBac"),
                        rs.getDate("sinhNhat") != null ? rs.getDate("sinhNhat").toLocalDate() : null
                    );
                }
            }
        }
        return null;
    }

    public List<KhachHang> getAllKhachHang() throws SQLException {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT * FROM khachhang";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new KhachHang(
                    rs.getInt("id"),
                    rs.getString("hoTen"),
                    rs.getString("soDienThoai"),
                    rs.getInt("diemTichLuy"),
                    rs.getString("capBac"),
                    rs.getDate("sinhNhat") != null ? rs.getDate("sinhNhat").toLocalDate() : null
                ));
            }
        }
        return list;
    }
}