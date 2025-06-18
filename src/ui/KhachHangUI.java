package ui;

import dao.KhachHangDAO;
import model.KhachHang;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KhachHangUI extends JFrame {
    private int idTaiKhoan;
    private String tenNhanVien;
    private KhachHangDAO khachHangDAO;
    private Map<String, String> birthdayMessages = new HashMap<>();
    private JTextArea messageArea;
    private JPanel editPanel; // Khai báo editPanel làm biến toàn cục
    private static final String MESSAGE_DIR = "sinhnhat"; // Thư mục chứa file txt

    public KhachHangUI(int idTaiKhoan, String tenNhanVien) {
        this.idTaiKhoan = idTaiKhoan;
        this.tenNhanVien = tenNhanVien;
        this.khachHangDAO = new KhachHangDAO();

        setTitle("Quản Lý Khách Hàng - " + tenNhanVien);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        File dir = new File(MESSAGE_DIR);
        if (!dir.exists()) {
            dir.mkdir();
        }

        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        editPanel = new JPanel(new BorderLayout()); // Khởi tạo editPanel
        editPanel.setOpaque(false);

        String[] capBacList = {"Dong", "Bac", "Vang", "Bach Kim", "Kim Cuong", "Thanh Vien"};
        JComboBox<String> capBacCombo = new JComboBox<>(capBacList);
        capBacCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        capBacCombo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 120, 215), 1),
            BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));
        capBacCombo.addActionListener(e -> {
            String selectedCapBac = (String) capBacCombo.getSelectedItem();
            String message = birthdayMessages.getOrDefault(selectedCapBac.toLowerCase(), 
                "Chúc mừng sinh nhật! Cảm ơn bạn đã ghé thăm 2MN với cấp bậc " + selectedCapBac + ".");
            messageArea.setText(message);
        });

        messageArea = new JTextArea(15, 30);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 120, 215), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        String initialCapBac = (String) capBacCombo.getSelectedItem();
        messageArea.setText(birthdayMessages.getOrDefault(initialCapBac.toLowerCase(), 
            "Chúc mừng sinh nhật! Cảm ơn bạn đã ghé thăm 2MN với cấp bậc " + initialCapBac + "."));

        JButton updateButton = new JButton("Cập nhật");
        updateButton.setFont(new Font("Arial", Font.BOLD, 14));
        updateButton.setBackground(new Color(0, 120, 215));
        updateButton.setForeground(Color.WHITE);
        updateButton.setFocusPainted(false);
        updateButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        updateButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        updateButton.addActionListener(e -> {
            String selectedCapBac = (String) capBacCombo.getSelectedItem();
            saveMessageToFile(selectedCapBac.toLowerCase(), messageArea.getText());
            refreshMessagesFromFiles(); // Tải lại dữ liệu từ file sau khi cập nhật
            JOptionPane.showMessageDialog(this, "Mẫu tin nhắn cho " + selectedCapBac + " đã được cập nhật!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(updateButton);

        JButton sendTestButton = new JButton("Gửi tin nhắn thử");
        sendTestButton.setFont(new Font("Arial", Font.BOLD, 14));
        sendTestButton.setBackground(new Color(0, 150, 136));
        sendTestButton.setForeground(Color.WHITE);
        sendTestButton.setFocusPainted(false);
        sendTestButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        sendTestButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendTestButton.addActionListener(e -> {
            refreshMessagesFromFiles(); // Tải lại dữ liệu từ file trước khi gửi
            sendTestMessage();
        });
        buttonPanel.add(sendTestButton);

        editPanel.add(capBacCombo, BorderLayout.NORTH);
        editPanel.add(new JScrollPane(messageArea), BorderLayout.CENTER);
        editPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        JButton backButton = new JButton("Quay lại");
        backButton.setFont(new Font("Roboto", Font.BOLD, 14));
        backButton.setBackground(new Color(108, 117, 125));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                backButton.setBackground(new Color(173, 181, 189));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                backButton.setBackground(new Color(108, 117, 125));
            }
        });
        backButton.addActionListener(e -> {
            dispose();
            new ChaoMungUI(idTaiKhoan, tenNhanVien);
        });
        buttonPanel.add(backButton, FlowLayout.LEFT);

        // Tải dữ liệu sau khi editPanel được khởi tạo
        refreshMessagesFromFiles();

        JPanel customerPanel = new JPanel(new BorderLayout());
        customerPanel.setOpaque(false);
        String[] columnNames = {"Mã KH", "Tên", "Ngày sinh", "Cấp bậc"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable customerTable = new JTable(tableModel);
        customerTable.setRowHeight(30);
        customerTable.setFont(new Font("Arial", Font.PLAIN, 12));
        customerTable.setGridColor(new Color(200, 200, 200));
        customerTable.setShowGrid(true);
        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh Sách Khách Hàng"));
        loadKhachHang(tableModel);

        customerPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(editPanel); 
        mainPanel.add(customerPanel); 

        add(mainPanel);
        setVisible(true);
    }

    private void loadKhachHang(DefaultTableModel tableModel) {
        try {
            List<KhachHang> khachHangList = khachHangDAO.getAllKhachHang();
            tableModel.setRowCount(0); 
            for (KhachHang kh : khachHangList) {
                Object[] row = {
                    kh.getId(),
                    kh.getTen(),
                    kh.getSinhNhat() != null ? kh.getSinhNhat().toString() : "",
                    kh.getCapBac()
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshMessagesFromFiles() {
        if (editPanel == null) return; // Kiểm tra nếu editPanel chưa khởi tạo
        birthdayMessages.clear(); // Xóa dữ liệu cũ
        String[] capBacList = {"dong", "bac", "vang", "bach kim", "kim cuong", "thanh vien"};
        for (String capBac : capBacList) {
            File file = new File(MESSAGE_DIR + "/" + capBac + ".txt");
            if (file.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                    birthdayMessages.put(capBac, content.toString().trim());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                birthdayMessages.put(capBac, "Chúc mừng sinh nhật! Cảm ơn bạn đã ghé thăm 2MN với cấp bậc " + capBac + ".");
            }
        }
        // Cập nhật lại messageArea với capBac hiện tại
        Component[] components = editPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JComboBox) {
                JComboBox<String> combo = (JComboBox<String>) comp;
                String selectedCapBac = (String) combo.getSelectedItem();
                if (selectedCapBac != null) {
                    String message = birthdayMessages.getOrDefault(selectedCapBac.toLowerCase(), 
                        "Chúc mừng sinh nhật! Cảm ơn bạn đã ghé thăm 2MN với cấp bậc " + selectedCapBac + ".");
                    messageArea.setText(message);
                }
                break;
            }
        }
    }

    private void loadBirthdayMessages() {
        refreshMessagesFromFiles(); // Sử dụng refreshMessagesFromFiles thay vì load riêng
    }

    private void saveMessageToFile(String capBac, String message) {
        File file = new File(MESSAGE_DIR + "/" + capBac + ".txt");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(message);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu file: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void checkAndSendBirthdayMessages() {
        try {
            List<KhachHang> khachHangList = khachHangDAO.getKhachHangSinhNhatHomNay();
            for (KhachHang kh : khachHangList) {
                String capBac = kh.getCapBac().toLowerCase();
                String message = birthdayMessages.getOrDefault(capBac, 
                    "Chúc mừng sinh nhật! Cảm ơn bạn đã ghé thăm 2MN.");
                sendSMS(kh.getSoDienThoai(), message);
                JOptionPane.showMessageDialog(this, "Đã gửi tin nhắn chúc mừng sinh nhật cho " + kh.getTen());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi kiểm tra sinh nhật: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void sendTestMessage() {
        String phoneNumber = JOptionPane.showInputDialog(this, "Nhập số điện thoại để thử:", "");
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            String message = messageArea.getText().trim().isEmpty() ? 
                "Chúc mừng sinh nhật! Đây là tin nhắn thử." : messageArea.getText();
            sendSMS(phoneNumber, message);
            JOptionPane.showMessageDialog(this, "Đã gửi tin nhắn thử đến " + phoneNumber);
        } else {
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Hàm giả lập gửi SMS (cần tích hợp API thực tế)
    private void sendSMS(String soDienThoai, String message) {
        System.out.println("Gửi SMS đến " + soDienThoai + ": " + message);
        // Thay bằng API gửi SMS thực tế (ví dụ: Twilio, Viettel)
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new KhachHangUI(1, "Nhất Nguyễn"));
    }
}