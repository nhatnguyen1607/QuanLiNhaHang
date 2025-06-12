package ui;

import dao.BanAnDAO;
import model.BanAn;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;

public class ChaoMungUI extends JFrame {
    private int idTaiKhoan;
    private String tenNhanVien;
    private BanAnDAO banAnDAO;

    public ChaoMungUI(int idTaiKhoan, String tenNhanVien) {
        this.idTaiKhoan = idTaiKhoan;
        this.tenNhanVien = tenNhanVien;
        this.banAnDAO = new BanAnDAO();

        setTitle("Chào Mừng - Quản Lý Nhà Hàng");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);

        // Panel chính với gradient nền
        JPanel backgroundPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, new Color(240, 248, 255), 0, getHeight(), new Color(245, 245, 220));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setOpaque(false);
        setContentPane(backgroundPanel);

        // Panel tiêu đề
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        JLabel titleLabel = new JLabel("Nhà Hàng 2MN", JLabel.CENTER);
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 36));
        titleLabel.setForeground(new Color(0, 102, 204));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        JLabel chaoLabel = new JLabel("Chào, " + tenNhanVien, JLabel.RIGHT);
        chaoLabel.setFont(new Font("Roboto", Font.PLAIN, 18));
        chaoLabel.setForeground(new Color(0, 102, 204));
        topPanel.add(chaoLabel, BorderLayout.EAST);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        backgroundPanel.add(topPanel, gbc);

        // Panel danh sách bàn
        JPanel banAnPanel = new JPanel(new GridLayout(0, 4, 20, 20));
        banAnPanel.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(banAnPanel);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0, 102, 204), 2),
            "Danh Sách Bàn", 0, 0, new Font("Roboto", Font.BOLD, 22), new Color(0, 102, 204)
        ));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        gbc.weighty = 1.0;
        gbc.gridy = 1;
        backgroundPanel.add(scrollPane, gbc);

        // Load danh sách bàn
        loadBanAn(banAnPanel);

        setVisible(true);
    }

    private void loadBanAn(JPanel banAnPanel) {
        try {
            List<BanAn> banAnList = banAnDAO.getAllBanAn();
            banAnPanel.removeAll();
            for (BanAn banAn : banAnList) {
                JPanel banBox = createBanBox(banAn);
                banAnPanel.add(banBox);
            }
            banAnPanel.revalidate();
            banAnPanel.repaint();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JPanel createBanBox(BanAn banAn) {
        JPanel box = new JPanel(new BorderLayout());
        box.setPreferredSize(new Dimension(150, 100));
        box.setBackground(banAn.getTrangThai().equals("DangPhucVu") ? new Color(255, 204, 204) : new Color(204, 255, 204));
        box.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel label = new JLabel("Bàn " + banAn.getId() + " (" + banAn.getTrangThai() + ")", JLabel.CENTER);
        label.setFont(new Font("Roboto", Font.BOLD, 16));
        label.setForeground(new Color(51, 51, 51));
        box.add(label, BorderLayout.CENTER);

        box.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (banAn.getTrangThai().equals("DangPhucVu")) {
                    new GiaoDienChinhUI(idTaiKhoan, tenNhanVien, banAn.getId()).setVisible(true);
                } else {
                    new GiaoDienChinhUI(idTaiKhoan, tenNhanVien, banAn.getId()).setVisible(true);
                }
                dispose();
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                box.setBackground(banAn.getTrangThai().equals("DangPhucVu") ? new Color(255, 153, 153) : new Color(153, 255, 153));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                box.setBackground(banAn.getTrangThai().equals("DangPhucVu") ? new Color(255, 204, 204) : new Color(204, 255, 204));
            }
        });

        return box;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new ChaoMungUI(1, "Nhất Nguyễn"));
    }
}