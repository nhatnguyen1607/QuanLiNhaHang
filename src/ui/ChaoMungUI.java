package ui;

import dao.BanAnDAO;
import model.BanAn;
import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.net.URL;

public class ChaoMungUI extends JFrame implements Serializable {
    private static final long serialVersionUID = 1L;
    private JList<BanAn> banAnList;
    private DefaultListModel<BanAn> listModel;
    private JButton batDauButton;
    private BanAnDAO banAnDAO;

    public ChaoMungUI() {
        banAnDAO = new BanAnDAO();
        setTitle("Chào Mừng - Quản Lý Nhà Hàng");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Tạo panel với ảnh nền
        BackgroundPanel backgroundPanel = new BackgroundPanel("/image/background.png");
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        // Tiêu đề
        JLabel titleLabel = new JLabel("Chào Mừng Đến Với Nhà Hàng", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(0, 0, 0, 150)); // Nền mờ
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        backgroundPanel.add(titleLabel, BorderLayout.NORTH);

        // Danh sách bàn với kích thước nhỏ hơn
        listModel = new DefaultListModel<>();
        banAnList = new JList<>(listModel);
        banAnList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        banAnList.setCellRenderer(new BanAnRenderer());
        loadBanAnData();
        banAnList.setFont(new Font("Arial", Font.PLAIN, 18));
        banAnList.setBackground(new Color(255, 255, 255, 200)); // Nền mờ
        banAnList.setForeground(Color.BLACK);
        JScrollPane scrollPane = new JScrollPane(banAnList);
        scrollPane.setPreferredSize(new Dimension(300, 400)); // Kích thước nhỏ hơn: 300x400 pixel
        scrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.WHITE), "Danh Sách Bàn", 0, 0, new Font("Arial", Font.BOLD, 20), Color.WHITE));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        // Đặt danh sách vào panel trung tâm để căn giữa
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 0, 20, 0); // Khoảng cách
        centerPanel.add(scrollPane, gbc);
        backgroundPanel.add(centerPanel, BorderLayout.CENTER);

        // Nút "Bắt đầu sử dụng"
        batDauButton = new JButton("Bắt Đầu Sử Dụng");
        batDauButton.setFont(new Font("Arial", Font.BOLD, 20));
        batDauButton.setBackground(new Color(50, 168, 82)); // Màu xanh lá
        batDauButton.setForeground(Color.WHITE);
        batDauButton.setFocusPainted(false);
        batDauButton.addActionListener(e -> batDauSuDung());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(batDauButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void loadBanAnData() {
        listModel.clear();
        java.util.List<BanAn> banAnListData = banAnDAO.getAllBanAn();
        for (BanAn banAn : banAnListData) {
            listModel.addElement(banAn);
        }
    }

    private void batDauSuDung() {
        BanAn selectedBan = banAnList.getSelectedValue();
        if (selectedBan == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một bàn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        selectedBan.setTrangThai("DangPhucVu");
        banAnDAO.updateBanAn(selectedBan);
        new GiaoDienChinhUI(selectedBan.getId()); // Truyền id_ban sang giao diện chính
        dispose();
    }

    private class BanAnRenderer extends DefaultListCellRenderer implements Serializable {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            BanAn banAn = (BanAn) value;
            label.setText("Bàn " + banAn.getId() + " - Trạng thái: " + banAn.getTrangThai());
            label.setFont(new Font("Arial", Font.PLAIN, 18));
            if (isSelected) {
                label.setBackground(new Color(50, 168, 82)); // Màu xanh lá khi chọn
                label.setForeground(Color.WHITE);
            } else {
                label.setBackground(new Color(255, 255, 255, 200));
                label.setForeground(Color.BLACK);
            }
            return label;
        }
    }

    // Class để hiển thị ảnh nền
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
                backgroundImage = null; // Đặt null để tránh crash
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                g.setColor(Color.GRAY); // Màu nền mặc định nếu ảnh không tải được
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChaoMungUI::new);
    }
}