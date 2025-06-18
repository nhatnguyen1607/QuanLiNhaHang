package ui;

import dao.BanAnDAO;
import dao.TaiKhoanDAO;
import model.BanAn;
import utils.HashUtil;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;

public class ChaoMungUI extends JFrame {
	private int idTaiKhoan;
	private String tenNhanVien;
	private BanAnDAO banAnDAO;
	private TaiKhoanDAO taiKhoanDAO;

	public ChaoMungUI(int idTaiKhoan, String tenNhanVien) {
		this.idTaiKhoan = idTaiKhoan;
		this.tenNhanVien = tenNhanVien;
		this.banAnDAO = new BanAnDAO();
		this.taiKhoanDAO = new TaiKhoanDAO();

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
				GradientPaint gradient = new GradientPaint(0, 0, new Color(240, 248, 255), 0, getHeight(),
						new Color(245, 245, 220));
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

		// Thêm Dropdown và chaoLabel trong cùng một panel con
		JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		rightPanel.setOpaque(false);

		JLabel chaoLabel = new JLabel("Chào, " + tenNhanVien);
		chaoLabel.setFont(new Font("Roboto", Font.PLAIN, 18));
		chaoLabel.setForeground(new Color(0, 102, 204));
		rightPanel.add(chaoLabel);

		// Thêm Dropdown với thiết kế đẹp hơn
		String[] options = { "Chọn tùy chọn", "Đổi mật khẩu", "Khách hàng", "Đăng xuất" };
		JComboBox<String> optionCombo = new JComboBox<>(options);
		optionCombo.setFont(new Font("Roboto", Font.PLAIN, 14)); // Font nhỏ hơn
		optionCombo.setPreferredSize(new Dimension(150, 25)); // Kích thước nhỏ hơn
		optionCombo.setBackground(new Color(255, 255, 255));
		optionCombo
				.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(0, 120, 215), 1),
						BorderFactory.createEmptyBorder(2, 5, 2, 5)));
		optionCombo.addActionListener(e -> {
			String selected = (String) optionCombo.getSelectedItem();
			if ("Đổi mật khẩu".equals(selected)) {
				showChangePasswordDialog();
			} else if ("Khách hàng".equals(selected)) {
				new KhachHangUI(idTaiKhoan, tenNhanVien).setVisible(true);
				dispose();
			}else if ("Đăng xuất".equals(selected)) {
				new DangNhapUI().setVisible(true);
				dispose();
			}
		});
		rightPanel.add(optionCombo);

		topPanel.add(rightPanel, BorderLayout.EAST);

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
		scrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 2),
				"Danh Sách Bàn", 0, 0, new Font("Roboto", Font.BOLD, 22), new Color(0, 102, 204)));
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
		box.setBackground(
				banAn.getTrangThai().equals("DangPhucVu") ? new Color(255, 204, 204) : new Color(204, 255, 204));
		box.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));

		JLabel label = new JLabel("Bàn " + banAn.getId() + " (" + banAn.getTrangThai() + ")", JLabel.CENTER);
		label.setFont(new Font("Roboto", Font.BOLD, 16));
		label.setForeground(new Color(51, 51, 51));
		box.add(label, BorderLayout.CENTER);

		box.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e)  {
				if (banAn.getTrangThai().equals("DangPhucVu")) {
					new GiaoDienChinhUI(idTaiKhoan, tenNhanVien, banAn.getId()).setVisible(true);
				} else {
					new GiaoDienChinhUI(idTaiKhoan, tenNhanVien, banAn.getId()).setVisible(true);
				}
				dispose();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				box.setBackground(banAn.getTrangThai().equals("DangPhucVu") ? new Color(255, 153, 153)
						: new Color(153, 255, 153));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				box.setBackground(banAn.getTrangThai().equals("DangPhucVu") ? new Color(255, 204, 204)
						: new Color(204, 255, 204));
			}
		});

		return box;
	}

	private void showChangePasswordDialog() {
		JDialog dialog = new JDialog(this, "Đổi Mật Khẩu", true);
		dialog.setLayout(new GridBagLayout());
		dialog.setSize(550, 350);
		dialog.getContentPane().setBackground(new Color(240, 248, 255)); // Nền gradient nhẹ

		// Thêm gradient cho dialog
		JPanel contentPanel = new JPanel(new GridBagLayout()) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				GradientPaint gradient = new GradientPaint(0, 0, new Color(240, 248, 255), 0, getHeight(),
						new Color(230, 240, 245));
				g2d.setPaint(gradient);
				g2d.fillRect(0, 0, getWidth(), getHeight());
			}
		};
		contentPanel.setOpaque(false);
		dialog.setContentPane(contentPanel);

		// Đặt vị trí giữa màn hình
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screenSize.width - dialog.getWidth()) / 2;
		int y = (screenSize.height - dialog.getHeight()) / 2;
		dialog.setLocation(x, y);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(15, 15, 15, 15);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.CENTER;

		// Tiêu đề
		JLabel titleLabel = new JLabel("Đổi Mật Khẩu");
		titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
		titleLabel.setForeground(new Color(0, 102, 204));
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		gbc.gridy = 0;
		contentPanel.add(titleLabel, gbc);

		// Mật khẩu cũ
		JLabel oldPassLabel = new JLabel("Mật khẩu cũ:");
		oldPassLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		oldPassLabel.setForeground(new Color(0, 102, 204));
		JPasswordField oldPassField = new JPasswordField(15);
		oldPassField
				.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(0, 120, 215), 1),
						BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy = 1;
		contentPanel.add(oldPassLabel, gbc);
		gbc.gridx = 1;
		contentPanel.add(oldPassField, gbc);

		// Mật khẩu mới
		JLabel newPassLabel = new JLabel("Mật khẩu mới:");
		newPassLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		newPassLabel.setForeground(new Color(0, 102, 204));
		JPasswordField newPassField = new JPasswordField(15);
		newPassField
				.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(0, 120, 215), 1),
						BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		gbc.gridx = 0;
		gbc.gridy = 2;
		contentPanel.add(newPassLabel, gbc);
		gbc.gridx = 1;
		contentPanel.add(newPassField, gbc);

		// Xác nhận mật khẩu mới
		JLabel confirmPassLabel = new JLabel("Xác nhận mật khẩu:");
		confirmPassLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		confirmPassLabel.setForeground(new Color(0, 102, 204));
		JPasswordField confirmPassField = new JPasswordField(15);
		confirmPassField
				.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(0, 120, 215), 1),
						BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		gbc.gridx = 0;
		gbc.gridy = 3;
		contentPanel.add(confirmPassLabel, gbc);
		gbc.gridx = 1;
		contentPanel.add(confirmPassField, gbc);

		// Nút xác nhận
		JButton confirmButton = new JButton("Xác nhận");
		confirmButton.setFont(new Font("Arial", Font.BOLD, 14));
		confirmButton.setBackground(new Color(0, 120, 215));
		confirmButton.setForeground(Color.WHITE);
		confirmButton.setFocusPainted(false);
		confirmButton
				.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(0, 100, 200), 1),
						BorderFactory.createEmptyBorder(8, 20, 8, 20)));
		confirmButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		confirmButton.addActionListener(e -> {
			String oldPass = new String(oldPassField.getPassword());
			String newPass = new String(newPassField.getPassword());
			String confirmPass = new String(confirmPassField.getPassword());

			if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
				JOptionPane.showMessageDialog(dialog, "Vui lòng điền đầy đủ thông tin!", "Lỗi",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (!newPass.equals(confirmPass)) {
				JOptionPane.showMessageDialog(dialog, "Mật khẩu mới không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
				return;
			}

			try {
				// Mã hóa mật khẩu cũ và mới bằng SHA-256
				String oldPassHashed = HashUtil.sha256(oldPass);
				String newPassHashed = HashUtil.sha256(newPass);

				// Kiểm tra mật khẩu cũ trong database
				String storedPass = taiKhoanDAO.getMatKhau(idTaiKhoan);
				if (!oldPassHashed.equals(storedPass)) {
					JOptionPane.showMessageDialog(dialog, "Mật khẩu cũ không đúng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
					return;
				}

				// Cập nhật mật khẩu mới
				taiKhoanDAO.updateMatKhau(idTaiKhoan, newPassHashed);
				JOptionPane.showMessageDialog(dialog, "Đổi mật khẩu thành công!", "Thành công",
						JOptionPane.INFORMATION_MESSAGE);
				dialog.dispose();
			} catch (SQLException ex) {
				JOptionPane.showMessageDialog(dialog, "Lỗi khi đổi mật khẩu: " + ex.getMessage(), "Lỗi",
						JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
		});
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		gbc.gridy = 4;
		contentPanel.add(confirmButton, gbc);

		dialog.setVisible(true);
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		SwingUtilities.invokeLater(() -> new ChaoMungUI(1, "Nhật Nguyễn"));
	}
}