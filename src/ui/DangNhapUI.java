package ui;

import dao.TaiKhoanDAO;
import dao.NhanVienDAO;
import model.TaiKhoan;
import utils.HashUtil;
import model.NhanVien;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.sql.SQLException;

public class DangNhapUI extends JFrame {
    private JTextField emailField;
    private JPasswordField matKhauField;
    private TaiKhoanDAO taiKhoanDAO;
    private NhanVienDAO nhanVienDAO;

    public DangNhapUI() {
        taiKhoanDAO = new TaiKhoanDAO();
        nhanVienDAO = new NhanVienDAO();

        setTitle("Đăng Nhập Hệ Thống");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); 
        setLocationRelativeTo(null);
        setResizable(true);

        BackgroundPanel backgroundPanel = new BackgroundPanel("/image/login_background.jpg");
        backgroundPanel.setLayout(new GridBagLayout());
        setContentPane(backgroundPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20); 
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Đăng Nhập Hệ Thống");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40)); 
        titleLabel.setForeground(new Color(255, 165, 0)); 
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        backgroundPanel.add(titleLabel, gbc);

        // Label và TextField Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        emailLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        backgroundPanel.add(emailLabel, gbc);

        emailField = new JTextField(20);
        emailField.setFont(new Font("Arial", Font.PLAIN, 18));
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255), 1, true),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));
        emailField.setBackground(new Color(255, 255, 255, 180));
        gbc.gridx = 1;
        gbc.gridy = 1;
        backgroundPanel.add(emailField, gbc);

        JLabel matKhauLabel = new JLabel("Mật khẩu:");
        matKhauLabel.setFont(new Font("Arial", Font.PLAIN, 20)); 
        matKhauLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 2;
        backgroundPanel.add(matKhauLabel, gbc);

        matKhauField = new JPasswordField(20);
        matKhauField.setFont(new Font("Arial", Font.PLAIN, 18)); 
        matKhauField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255), 1, true),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));
        matKhauField.setBackground(new Color(255, 255, 255, 180));
        gbc.gridx = 1;
        gbc.gridy = 2;
        backgroundPanel.add(matKhauField, gbc);

        JButton dangNhapButton = new JButton("Đăng Nhập");
        dangNhapButton.setFont(new Font("Arial", Font.BOLD, 20)); 
        dangNhapButton.setBackground(new Color(255, 165, 0)); 
        dangNhapButton.setForeground(Color.WHITE);
        dangNhapButton.setFocusPainted(false);
        dangNhapButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 165, 0), 2, true),
            BorderFactory.createEmptyBorder(15, 40, 15, 40) 
        ));
        dangNhapButton.addActionListener(e -> dangNhap());
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        backgroundPanel.add(dangNhapButton, gbc);

        setVisible(true);
    }


    private void dangNhap() {
        String email = emailField.getText().trim();
        String matKhau = new String(matKhauField.getPassword());

        String hashedMatKhau = HashUtil.sha256(matKhau);

        try {
            TaiKhoan taiKhoan = taiKhoanDAO.getTaiKhoanByEmailAndMatKhau(email, hashedMatKhau);

            if (taiKhoan != null && "nhanvien".equals(taiKhoan.getVaiTro()) && "mo".equals(taiKhoan.getTrangThai())) {
                int idTaiKhoan = taiKhoan.getId_taikhoan();
                NhanVien nhanVien = nhanVienDAO.getNhanVienByIdTaiKhoan(idTaiKhoan);

                if (nhanVien != null) {
                    String tenNhanVien = nhanVien.getTen();
                    dispose();
                    new ChaoMungUI(idTaiKhoan, tenNhanVien); // Chuyển sang giao diện chọn bàn
                } else {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin nhân viên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Email, mật khẩu, vai trò hoặc trạng thái không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối cơ sở dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            try {
                URL url = getClass().getResource(imagePath);
                if (url == null) {
                    throw new IllegalArgumentException("Không tìm thấy tài nguyên: " + imagePath);
                }
                backgroundImage = new ImageIcon(url).getImage();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Lỗi tải ảnh nền: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                backgroundImage = null;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                g.setColor(new Color(245, 222, 179)); // Màu nền mặc định (beige)
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DangNhapUI::new);
    }
}