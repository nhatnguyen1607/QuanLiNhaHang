package ui;

import dao.ThuMucMonAnDAO;
import dao.MonAnDAO;
import model.ThuMucMonAn;
import model.MonAn;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.List;

public class GiaoDienChinhUI extends JFrame {
    private int idBan;
    private JList<ThuMucMonAn> thuMucList;
    private DefaultListModel<ThuMucMonAn> thuMucModel;
    private JPanel monAnPanel;
    private ThuMucMonAnDAO thuMucMonAnDAO;
    private MonAnDAO monAnDAO;

    public GiaoDienChinhUI(int idBan) {
        this.idBan = idBan;
        thuMucMonAnDAO = new ThuMucMonAnDAO();
        monAnDAO = new MonAnDAO();

        setTitle("Giao Diện Chính - Quản Lý Nhà Hàng");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        // Panel chính với ảnh nền
        BackgroundPanel backgroundPanel = new BackgroundPanel("/image/background.png");
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        // Tiêu đề
        JLabel titleLabel = new JLabel("Chọn Món Ăn - Bàn " + idBan, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(0, 0, 0, 150));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        backgroundPanel.add(titleLabel, BorderLayout.NORTH);

        // Panel chia đôi: Thư mục bên trái, món ăn bên phải
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setOpaque(false);
        splitPane.setDividerLocation(300);

        // Danh sách thư mục (bên trái)
        thuMucModel = new DefaultListModel<>();
        thuMucList = new JList<>(thuMucModel);
        thuMucList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        thuMucList.setCellRenderer(new ThuMucRenderer());
        thuMucList.setFont(new Font("Arial", Font.PLAIN, 18));
        thuMucList.setBackground(new Color(255, 255, 255, 200));
        thuMucList.setForeground(Color.BLACK);
        loadThuMucData();
        thuMucList.addListSelectionListener(e -> loadMonAnData());
        JScrollPane thuMucScrollPane = new JScrollPane(thuMucList);
        thuMucScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.WHITE), "Thư Mục Món Ăn", 0, 0, new Font("Arial", Font.BOLD, 20), Color.WHITE));
        thuMucScrollPane.setOpaque(false);
        thuMucScrollPane.getViewport().setOpaque(false);
        splitPane.setLeftComponent(thuMucScrollPane);

        // Danh sách món ăn (bên phải)
        monAnPanel = new JPanel();
        monAnPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        monAnPanel.setOpaque(false);
        JScrollPane monAnScrollPane = new JScrollPane(monAnPanel);
        monAnScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.WHITE), "Danh Sách Món Ăn", 0, 0, new Font("Arial", Font.BOLD, 20), Color.WHITE));
        monAnScrollPane.setOpaque(false);
        monAnScrollPane.getViewport().setOpaque(false);
        splitPane.setRightComponent(monAnScrollPane);

        backgroundPanel.add(splitPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private void loadThuMucData() {
        thuMucModel.clear();
        List<ThuMucMonAn> thuMucListData = thuMucMonAnDAO.getAllThuMucMonAn();
        for (ThuMucMonAn thuMuc : thuMucListData) {
            thuMucModel.addElement(thuMuc);
        }
    }

    private void loadMonAnData() {
        ThuMucMonAn selectedThuMuc = thuMucList.getSelectedValue();
        if (selectedThuMuc == null) return;

        monAnPanel.removeAll(); // Xóa các món ăn hiện tại
        List<MonAn> monAnListData = monAnDAO.getMonAnByThuMuc(selectedThuMuc.getId_thumuc());
        for (MonAn monAn : monAnListData) {
            monAnPanel.add(createMonAnBox(monAn));
        }
        monAnPanel.revalidate();
        monAnPanel.repaint();
        // Làm mới toàn bộ giao diện để tránh chồng lấp
        getContentPane().repaint();
    }

    private JPanel createMonAnBox(MonAn monAn) {
        JPanel box = new JPanel();
        box.setLayout(new BorderLayout());
        box.setPreferredSize(new Dimension(200, 250));
        box.setBackground(new Color(255, 255, 255, 200));
        box.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        // Hình ảnh món ăn
        JLabel imageLabel = new JLabel();
        try {
            String imagePath = "/image/" + monAn.getHinhAnh();
            ImageIcon icon = new ImageIcon(getClass().getResource(imagePath));
            Image scaledImage = icon.getImage().getScaledInstance(180, 150, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            e.printStackTrace();
            imageLabel.setText("Hình ảnh không tải được");
            imageLabel.setForeground(Color.RED);
        }
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        box.add(imageLabel, BorderLayout.CENTER);

        // Tên món ăn
        JLabel nameLabel = new JLabel(monAn.getTenMon(), JLabel.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setForeground(Color.BLACK);
        box.add(nameLabel, BorderLayout.NORTH);

        // Giá
        JLabel priceLabel = new JLabel(monAn.getGia() + " VNĐ", JLabel.CENTER);
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        priceLabel.setForeground(Color.BLACK);
        box.add(priceLabel, BorderLayout.SOUTH);

        return box;
    }

    private class ThuMucRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            ThuMucMonAn thuMuc = (ThuMucMonAn) value;
            label.setText(thuMuc.getTen_thumuc());
            label.setFont(new Font("Arial", Font.PLAIN, 18));
            if (isSelected) {
                label.setBackground(new Color(50, 168, 82));
                label.setForeground(Color.WHITE);
            } else {
                label.setBackground(new Color(255, 255, 255, 200));
                label.setForeground(Color.BLACK);
            }
            return label;
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
            // Xóa nền trước khi vẽ để tránh chồng lấp
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                g.setColor(Color.GRAY);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GiaoDienChinhUI(1));
    }
}