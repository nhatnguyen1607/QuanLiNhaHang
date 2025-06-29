package ui;

import dao.ThuMucMonAnDAO;
import dao.MonAnDAO;
import dao.MonAnNguyenLieuDAO;
import dao.NguyenLieuDAO;
import dao.NhanVienDAO;
import dao.OrderDAO;
import dao.ChiTietOrderDAO;
import dao.BanAnDAO;
import dao.KhachHangDAO;
import dao.HoaDonDAO;
import dao.LuongDAO;
import dao.XepHangKhachHangDAO;
import dao.ChiTietHoaDonDAO; // Thêm import mới
import model.ThuMucMonAn;
import model.MonAn;
import model.MonAnNguyenLieu;
import model.NguyenLieu;
import model.NhanVien;
import model.GioHangItem;
import model.Order;
import model.BanAn;
import model.ChiTietOrder;
import model.KhachHang;
import model.Luong;
import model.HoaDon;
import model.XepHangKhachHang;
import model.ChiTietHoaDon; // Thêm import mới
import vnpay.Config;
import vnpay.ajaxServlet;
import vnpay.vnpayQuery;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import com.google.gson.JsonObject;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class GiaoDienChinhUI extends JFrame {
	private int idTaiKhoan;
	private String tenNhanVien;
	private int idBan;
	private JComboBox<ThuMucMonAn> thuMucComboBox;
	private JPanel monAnPanel;
	private JTable gioHangTable;
	private DefaultTableModel gioHangModel;
	private ThuMucMonAnDAO thuMucMonAnDAO;
	private MonAnDAO monAnDAO;
	private OrderDAO orderDAO;
	private ChiTietOrderDAO chiTietOrderDAO;
	private NhanVienDAO nhanVienDAO;
	private BanAnDAO banAnDAO;
	private KhachHangDAO khachHangDAO;
	private HoaDonDAO hoaDonDAO;
	private LuongDAO luongDAO;
	private XepHangKhachHangDAO xepHangKhachHangDAO;
	private ChiTietHoaDonDAO chiTietHoaDonDAO;
	private MonAnNguyenLieuDAO monAnNguyenLieuDAO;
	private NguyenLieuDAO nguyenLieuDAO;
	private List<GioHangItem> gioHangList;
	private JSplitPane splitPane;
	private JLabel tongTienLabel;
	private JTextField ghiChuField;
	private int currentOrderId;

	public GiaoDienChinhUI(int idTaiKhoan, String tenNhanVien, int idBan) {
		this.idTaiKhoan = idTaiKhoan;
		this.tenNhanVien = tenNhanVien;
		this.idBan = idBan;
		this.thuMucMonAnDAO = new ThuMucMonAnDAO();
		this.monAnDAO = new MonAnDAO();
		this.orderDAO = new OrderDAO();
		this.chiTietOrderDAO = new ChiTietOrderDAO();
		this.banAnDAO = new BanAnDAO();
		this.khachHangDAO = new KhachHangDAO();
		this.nhanVienDAO = new NhanVienDAO();
		this.hoaDonDAO = new HoaDonDAO();
		this.luongDAO = new LuongDAO();
		this.xepHangKhachHangDAO = new XepHangKhachHangDAO();
		this.chiTietHoaDonDAO = new ChiTietHoaDonDAO();
		this.monAnNguyenLieuDAO = new MonAnNguyenLieuDAO();
		this.nguyenLieuDAO = new NguyenLieuDAO();
		this.gioHangList = new ArrayList<>();
		this.gioHangModel = new DefaultTableModel(
				new Object[] { "Hình ảnh", "Tên món", "Giá", "Số lượng", "Điều chỉnh" }, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 4;
			}
		};
		this.currentOrderId = -1;

		setTitle("Quản Lý Nhà Hàng - Bàn " + idBan);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
		JLabel chaoLabel = new JLabel("Chào, " + tenNhanVien, JLabel.RIGHT);
		chaoLabel.setFont(new Font("Roboto", Font.PLAIN, 18));
		chaoLabel.setForeground(new Color(0, 102, 204));
		topPanel.add(chaoLabel, BorderLayout.EAST);

		// Panel thư mục
		JPanel comboPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
		comboPanel.setOpaque(false);
		JLabel comboLabel = new JLabel("Danh Mục: ");
		comboLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
		comboLabel.setForeground(new Color(0, 102, 204));
		thuMucComboBox = new JComboBox<>();
		thuMucComboBox.setPreferredSize(new Dimension(250, 35));
		thuMucComboBox.setFont(new Font("Roboto", Font.PLAIN, 14));
		thuMucComboBox.setBackground(Color.WHITE);
		thuMucComboBox.setForeground(new Color(51, 51, 51));
		thuMucComboBox.addActionListener(e -> SwingUtilities.invokeLater(() -> {
			try {
				loadMonAnData();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}));
		comboPanel.add(comboLabel);
		comboPanel.add(thuMucComboBox);

		// Split pane
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setOpaque(false);
		splitPane.setResizeWeight(0.7);
		splitPane.setDividerSize(5);
		splitPane.setMinimumSize(new Dimension(600, 400));

		// Danh sách món ăn
		monAnPanel = new JPanel();
		monAnPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
		monAnPanel.setOpaque(false);
		JScrollPane monAnScrollPane = new JScrollPane(monAnPanel);
		monAnScrollPane
				.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 2),
						"Danh Sách Món Ăn", 0, 0, new Font("Roboto", Font.BOLD, 22), new Color(0, 102, 204)));
		monAnScrollPane.setOpaque(false);
		monAnScrollPane.getViewport().setOpaque(false);
		splitPane.setLeftComponent(monAnScrollPane);

		// Giỏ hàng
		gioHangTable = new JTable(gioHangModel) {
			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);
				if (c instanceof JComponent) {
					((JComponent) c).setOpaque(false);
				}
				c.setBackground(row % 2 == 0 ? new Color(240, 248, 255, 180) : new Color(245, 245, 220, 180));
				return c;
			}
		};
		gioHangTable.setRowHeight(70);
		gioHangTable.setFont(new Font("Roboto", Font.PLAIN, 14));
		gioHangTable.setForeground(new Color(51, 51, 51));
		gioHangTable.getColumnModel().getColumn(0).setPreferredWidth(60);
		gioHangTable.getColumnModel().getColumn(1).setPreferredWidth(120);
		gioHangTable.getColumnModel().getColumn(2).setPreferredWidth(100);
		gioHangTable.getColumnModel().getColumn(3).setPreferredWidth(30);
		gioHangTable.getColumnModel().getColumn(4).setPreferredWidth(150);
		gioHangTable.getColumnModel().getColumn(0).setCellRenderer(new ImageRenderer());
		gioHangTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor());
		gioHangTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
		gioHangTable.setOpaque(false);
		gioHangTable.setTableHeader(null);
		gioHangTable.setShowGrid(false);

		JScrollPane gioHangScrollPane = new JScrollPane(gioHangTable);
		gioHangScrollPane.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(new Color(0, 102, 204), 2), "Danh sách đặt món bàn " + idBan, 0, 0,
				new Font("Roboto", Font.BOLD, 22), new Color(0, 102, 204)));
		gioHangScrollPane.setOpaque(false);
		gioHangScrollPane.getViewport().setOpaque(false);
		splitPane.setRightComponent(gioHangScrollPane);

		// Panel chính layout
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		backgroundPanel.add(topPanel, gbc);

		gbc.gridy = 1;
		backgroundPanel.add(comboPanel, gbc);

		gbc.weighty = 1.0;
		gbc.gridy = 2;
		backgroundPanel.add(splitPane, gbc);

		// Phần hiển thị tổng tiền
		JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
		totalPanel.setOpaque(false);
		tongTienLabel = new JLabel("Tổng tiền: 0 VNĐ");
		tongTienLabel.setFont(new Font("Roboto", Font.BOLD, 16));
		tongTienLabel.setForeground(new Color(220, 53, 69));
		totalPanel.add(tongTienLabel);

		gbc.gridy = 3;
		backgroundPanel.add(totalPanel, gbc);

		// Panel cho ghi chú
		JPanel ghiChuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
		ghiChuPanel.setOpaque(false);
		JLabel ghiChuLabel = new JLabel("Ghi chú:");
		ghiChuLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
		ghiChuLabel.setForeground(new Color(0, 102, 204));
		ghiChuField = new JTextField(30);
		ghiChuField.setFont(new Font("Roboto", Font.PLAIN, 14));
		ghiChuField.setPreferredSize(new Dimension(300, 30));
		ghiChuPanel.add(ghiChuLabel);
		ghiChuPanel.add(ghiChuField);

		gbc.gridy = 4;
		backgroundPanel.add(ghiChuPanel, gbc);

		// Nút chức năng dưới giỏ hàng
		JPanel buttonPanel = new JPanel(new BorderLayout(20, 10));
		buttonPanel.setOpaque(false);

		JPanel leftButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		leftButtonPanel.setOpaque(false);
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
		leftButtonPanel.add(backButton);
		buttonPanel.add(leftButtonPanel, BorderLayout.WEST);

		JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
		rightButtonPanel.setOpaque(false);
		JButton datMonButton = new JButton("Đặt Món");
		datMonButton.setFont(new Font("Roboto", Font.BOLD, 14));
		datMonButton.setBackground(new Color(40, 167, 69));
		datMonButton.setForeground(Color.WHITE);
		datMonButton.setFocusPainted(false);
		datMonButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
		datMonButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		datMonButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				datMonButton.setBackground(new Color(46, 204, 113));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				datMonButton.setBackground(new Color(40, 167, 69));
			}
		});
		datMonButton.addActionListener(e -> datMon());
		rightButtonPanel.add(datMonButton);

		JButton xacNhanLenMonButton = new JButton("Xác nhận đã lên món");
		xacNhanLenMonButton.setFont(new Font("Roboto", Font.BOLD, 14));
		xacNhanLenMonButton.setBackground(new Color(0, 123, 255));
		xacNhanLenMonButton.setForeground(Color.WHITE);
		xacNhanLenMonButton.setFocusPainted(false);
		xacNhanLenMonButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
		xacNhanLenMonButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		xacNhanLenMonButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				xacNhanLenMonButton.setBackground(new Color(0, 141, 255));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				xacNhanLenMonButton.setBackground(new Color(0, 123, 255));
			}
		});
		xacNhanLenMonButton.addActionListener(e -> xacNhanLenMon());
		rightButtonPanel.add(xacNhanLenMonButton);

		JButton thanhToanButton = new JButton("Thanh Toán");
		thanhToanButton.setFont(new Font("Roboto", Font.BOLD, 14));
		thanhToanButton.setBackground(new Color(0, 123, 255));
		thanhToanButton.setForeground(Color.WHITE);
		thanhToanButton.setFocusPainted(false);
		thanhToanButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
		thanhToanButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		thanhToanButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				thanhToanButton.setBackground(new Color(0, 141, 255));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				thanhToanButton.setBackground(new Color(0, 123, 255));
			}
		});
		thanhToanButton.addActionListener(e -> showThanhToanDialog());
		rightButtonPanel.add(thanhToanButton);
		buttonPanel.add(rightButtonPanel, BorderLayout.EAST);

		gbc.gridy = 5;
		backgroundPanel.add(buttonPanel, gbc);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				splitPane.setDividerLocation(0.7);
			}
		});

		loadAllOrdersForBan();

		SwingUtilities.invokeLater(() -> {
			try {
				loadThuMucData();
				if (thuMucComboBox.getItemCount() > 0) {
					thuMucComboBox.setSelectedIndex(0);
					loadMonAnData();
				}
				updateGioHangDisplay();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			splitPane.setDividerLocation(0.7);
		});

		setVisible(true);
	}

	private void loadThuMucData() throws SQLException {
		thuMucComboBox.removeAllItems();
		List<ThuMucMonAn> thuMucList = thuMucMonAnDAO.getAllThuMucMonAn();
		if (thuMucList.isEmpty()) {
			thuMucComboBox.addItem(new ThuMucMonAn());
			thuMucComboBox.setEnabled(false);
		} else {
			for (ThuMucMonAn thuMuc : thuMucList) {
				thuMucComboBox.addItem(thuMuc);
			}
			thuMucComboBox.setEnabled(true);
		}
	}

	private void loadMonAnData() throws SQLException {
		if (monAnPanel != null) {
			monAnPanel.removeAll();
			ThuMucMonAn selectedThuMuc = (ThuMucMonAn) thuMucComboBox.getSelectedItem();
			if (selectedThuMuc != null && (thuMucComboBox.isEnabled() || selectedThuMuc.getId_thumuc() > 0)) {
				List<MonAn> monAnList = monAnDAO.getMonAnByThuMuc(selectedThuMuc.getId_thumuc());
				if (monAnList.isEmpty()) {
					JLabel noData = new JLabel("Không có món ăn", JLabel.CENTER);
					noData.setFont(new Font("Roboto", Font.ITALIC, 16));
					noData.setForeground(new Color(150, 150, 150));
					monAnPanel.add(noData);
				} else {
					for (MonAn monAn : monAnList) {
						monAnPanel.add(createMonAnBox(monAn));
					}
				}
			}
			monAnPanel.revalidate();
			monAnPanel.repaint();
		}
	}

	private JPanel createMonAnBox(MonAn monAn) {
		JPanel box = new JPanel();
		box.setLayout(new BorderLayout(0, 5));
		box.setPreferredSize(new Dimension(160, 200));
		box.setBackground(new Color(255, 255, 255));
		box.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));

		JLabel imageLabel = new JLabel();
		try {
			String imagePath = "/image/" + monAn.getHinhAnh();
			URL imageUrl = getClass().getResource(imagePath);
			if (imageUrl != null) {
				ImageIcon icon = new ImageIcon(imageUrl);
				Image scaledImage = icon.getImage().getScaledInstance(140, 100, Image.SCALE_SMOOTH);
				imageLabel.setIcon(new ImageIcon(scaledImage));
			} else {
				imageLabel.setText("Hình ảnh không có");
				imageLabel.setFont(new Font("Roboto", Font.ITALIC, 12));
				imageLabel.setForeground(new Color(150, 150, 150));
			}
		} catch (Exception e) {
			e.printStackTrace();
			imageLabel.setText("Lỗi tải ảnh");
			imageLabel.setFont(new Font("Roboto", Font.ITALIC, 12));
			imageLabel.setForeground(new Color(200, 0, 0));
		}
		imageLabel.setHorizontalAlignment(JLabel.CENTER);
		box.add(imageLabel, BorderLayout.CENTER);

		JPanel infoPanel = new JPanel(new BorderLayout());
		infoPanel.setOpaque(false);

		JLabel nameLabel = new JLabel(monAn.getTenMon(), JLabel.CENTER);
		nameLabel.setFont(new Font("Roboto", Font.BOLD, 12));
		nameLabel.setForeground(new Color(51, 51, 51));
		infoPanel.add(nameLabel, BorderLayout.NORTH);

		JLabel priceLabel = new JLabel(String.format("%,d VNĐ", monAn.getGia()), JLabel.CENTER);
		priceLabel.setFont(new Font("Roboto", Font.PLAIN, 12));
		priceLabel.setForeground(new Color(0, 102, 204));
		infoPanel.add(priceLabel, BorderLayout.SOUTH);

		// Kiểm tra trạng thái
		if ("Het".equals(monAn.getTrangThai())) {
			JLabel hetMonLabel = new JLabel("Hết món", JLabel.CENTER);
			hetMonLabel.setFont(new Font("Roboto", Font.BOLD, 12));
			hetMonLabel.setForeground(new Color(220, 53, 69));
			infoPanel.add(hetMonLabel, BorderLayout.CENTER);
			box.setBackground(new Color(200, 200, 200, 100)); // Làm mờ nền
		}

		box.add(infoPanel, BorderLayout.SOUTH);

		box.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				if (!"Het".equals(monAn.getTrangThai())) {
					addToGioHang(monAn);
				} else {
					JOptionPane.showMessageDialog(GiaoDienChinhUI.this, "Món " + monAn.getTenMon() + " đã hết!",
							"Thông báo", JOptionPane.WARNING_MESSAGE);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				if (!box.getBackground().equals(new Color(200, 200, 200, 100))) {
					box.setBackground(new Color(240, 248, 255));
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (!box.getBackground().equals(new Color(200, 200, 200, 100))) {
					box.setBackground(new Color(255, 255, 255));
				}
			}
		});

		return box;
	}

	private void addToGioHang(MonAn monAn) {
	    boolean foundMoi = false;
	    for (GioHangItem item : gioHangList) {
	        if (item.getMonAn().getId() == monAn.getId() && "Moi".equals(item.getTrangThai())) {
	            item.setSoLuong(item.getSoLuong() + 1);
	            foundMoi = true;
	            break;
	        }
	    }

	    if (!foundMoi) {
	        boolean foundCu = false;
	        for (GioHangItem item : gioHangList) {
	            if (item.getMonAn().getId() == monAn.getId() && "Cu".equals(item.getTrangThai())) {
	                foundCu = true;
	                break;
	            }
	        }
	        if (!foundCu) {
	            GioHangItem newItem = new GioHangItem(monAn, 1, "Moi");
	            gioHangList.add(newItem);
	        } else {
	            GioHangItem newItem = new GioHangItem(monAn, 1, "Moi");
	            gioHangList.add(newItem);
	        }
	    }
	    updateGioHangDisplay();
	}

	private void updateGioHangDisplay() {
		gioHangModel.setRowCount(0);
		long tongTien = 0;
		if (gioHangList.isEmpty()) {
			gioHangModel.setRowCount(0);
		} else {
			for (GioHangItem item : gioHangList) {
				MonAn monAn = item.getMonAn();
				ImageIcon icon = loadImageIcon(monAn.getHinhAnh());
				gioHangModel.addRow(new Object[] { icon, monAn.getTenMon(), String.format("%,d VNĐ", monAn.getGia()),
						item.getSoLuong(), null });
				tongTien += monAn.getGia() * item.getSoLuong();
			}
		}
		DecimalFormat df = new DecimalFormat("#,### VNĐ");
		tongTienLabel.setText("Tổng tiền: " + df.format(tongTien));
		gioHangTable.revalidate();
		gioHangTable.repaint();
	}

	private ImageIcon loadImageIcon(String hinhAnh) {
		try {
			String imagePath = "/image/" + hinhAnh;
			URL imageUrl = getClass().getResource(imagePath);
			if (imageUrl != null) {
				ImageIcon icon = new ImageIcon(imageUrl);
				Image scaledImage = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
				return new ImageIcon(scaledImage);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void datMon() {
	    if (gioHangList.isEmpty()) {
	        JOptionPane.showMessageDialog(this, "Giỏ hàng trống!", "Thông báo", JOptionPane.WARNING_MESSAGE);
	        return;
	    }

	    String ghiChu = ghiChuField.getText().trim();
	    try {
	    	NhanVien nhanVien = nhanVienDAO.getNhanVienByIdTaiKhoan(idTaiKhoan);
	    	 int id_nhanvien = nhanVien.getId_nhanvien();
	        if (currentOrderId == -1) {
	            Order order = new Order(0, idBan, id_nhanvien, LocalDateTime.now(), "DangDat",
	                    ghiChu.isEmpty() ? null : ghiChu);
	            currentOrderId = orderDAO.addOrder(order);
	            updateBanAnStatus("DangPhucVu");
	        } else if (!ghiChu.isEmpty()) {
	            orderDAO.updateOrderGhiChu(currentOrderId, ghiChu);
	        }

	        // Lấy danh sách chi tiết đơn hàng hiện tại trước khi xóa
	        List<ChiTietOrder> existingChiTietOrders = chiTietOrderDAO.getChiTietOrdersByOrderId(currentOrderId);

	        // Xóa tất cả chi tiết đơn hàng cũ
	        chiTietOrderDAO.deleteAllChiTietOrder(currentOrderId);

	        // Thêm lại chi tiết đơn hàng từ giỏ hàng hiện tại
	        for (GioHangItem item : gioHangList) {
	            MonAn monAn = item.getMonAn();
	            String trangThai = item.getTrangThai(); // Giữ trạng thái từ GioHangItem
	            ChiTietOrder chiTiet = new ChiTietOrder(0, currentOrderId, monAn.getId(), item.getSoLuong(), trangThai);
	            chiTietOrderDAO.addChiTietOrder(chiTiet);
	        }

	        // Cập nhật trạng thái Order
	        boolean hasMoi = gioHangList.stream().anyMatch(item -> "Moi".equals(item.getTrangThai()));
	        orderDAO.updateOrderStatus(currentOrderId, hasMoi ? "DangDat" : "DaXong");

	        JOptionPane.showMessageDialog(this, "Đặt món thành công cho Bàn " + idBan + "!", "Thông báo",
	                JOptionPane.INFORMATION_MESSAGE);
	        loadAllOrdersForBan();
	    } catch (SQLException e) {
	        JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật giỏ hàng: " + e.getMessage(), "Lỗi",
	                JOptionPane.ERROR_MESSAGE);
	        e.printStackTrace();
	    }
	}

	private void updateBanAnStatus(String trangThai) {
		try {
			BanAn banAn = banAnDAO.getBanAnById(idBan);
			if (banAn != null) {
				banAn.setTrangThai(trangThai);
				banAnDAO.updateBanAn(banAn);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadAllOrdersForBan() {
		try {
			List<Order> orders = orderDAO.getOrdersByBanAnId(idBan);
			gioHangList.clear();
			gioHangModel.setRowCount(0);
			currentOrderId = -1; // Reset trước khi kiểm tra
			for (Order order : orders) {
				if ("DangDat".equals(order.getTrangThai()) || "DaXong".equals(order.getTrangThai())) {
					currentOrderId = order.getId(); // Gán currentOrderId từ đơn hàng đầu tiên hợp lệ
					List<ChiTietOrder> chiTietOrders = chiTietOrderDAO.getChiTietOrdersByOrderId(order.getId());
					for (ChiTietOrder chiTiet : chiTietOrders) {
						MonAn monAn = monAnDAO.getMonAnById(chiTiet.getMonAnId());
						if (monAn != null) {
							GioHangItem item = new GioHangItem(monAn, chiTiet.getSoLuong(), chiTiet.getTrangThai());
							gioHangList.add(item);
						}
					}
					break; // Chỉ lấy đơn hàng đầu tiên hợp lệ
				}
			}
			updateGioHangDisplay();
			System.out.println("CurrentOrderId after load: " + currentOrderId); // Debug
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void xacNhanLenMon() {
	    if (currentOrderId == -1) {
	        JOptionPane.showMessageDialog(this, "Không có đơn hàng để xác nhận!", "Thông báo",
	                JOptionPane.WARNING_MESSAGE);
	        return;
	    }

	    try {
	        orderDAO.updateOrderStatus(currentOrderId, "DaXong");
	        List<ChiTietOrder> chiTietOrders = chiTietOrderDAO.getChiTietOrdersByOrderId(currentOrderId);
	        for (ChiTietOrder chiTiet : chiTietOrders) {
	            // Chỉ xử lý món có trạng thái "Moi"
	            if ("Moi".equals(chiTiet.getTrangThai())) {
	                List<MonAnNguyenLieu> monAnNguyenLieus = monAnNguyenLieuDAO.getMonAnNguyenLieuByMonAnId(chiTiet.getMonAnId());
	                boolean hetMon = false;
	                for (MonAnNguyenLieu item : monAnNguyenLieus) {
	                    NguyenLieu nguyenLieu = nguyenLieuDAO.getNguyenLieuById(item.getNguyenLieuId());
	                    if (nguyenLieu != null) {
	                        double soLuongSuDung = item.getSoLuongCan() * chiTiet.getSoLuong();
	                        double newSoLuongTon = nguyenLieu.getSoLuongTon() - soLuongSuDung;
	                        if (newSoLuongTon < 0) {
	                            JOptionPane.showMessageDialog(this, "Nguyên liệu " + nguyenLieu.getTenNguyenLieu() + " không đủ!", "Lỗi",
	                                    JOptionPane.ERROR_MESSAGE);
	                            return;
	                        }
	                        nguyenLieuDAO.updateSoLuongTon(item.getNguyenLieuId(), newSoLuongTon);
	                        if (item.getSoLuongCan() > newSoLuongTon) {
	                            hetMon = true;
	                        }
	                    }
	                }
	                // Cập nhật trạng thái món nếu hết
	                if (hetMon) {
	                    monAnDAO.updateMonAnTrangThai(chiTiet.getMonAnId(), "Het");
	                }
	            }
	        }
	        chiTietOrderDAO.updateChiTietOrderStatus(currentOrderId, "Cu"); // Chuyển tất cả sang "Cu" sau khi xác nhận
	        JOptionPane.showMessageDialog(this, "Xác nhận đã lên món thành công cho Bàn " + idBan + "!", "Thông báo",
	                JOptionPane.INFORMATION_MESSAGE);
	        loadAllOrdersForBan(); // Cập nhật lại danh sách để phản ánh trạng thái mới
	    } catch (SQLException e) {
	        JOptionPane.showMessageDialog(this, "Lỗi khi xác nhận: " + e.getMessage(), "Lỗi",
	                JOptionPane.ERROR_MESSAGE);
	        e.printStackTrace();
	    }
	    try {
	        loadMonAnData();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	private void showThanhToanDialog() {
	    if (currentOrderId == -1) {
	        JOptionPane.showMessageDialog(this, "Không có đơn hàng để thanh toán!", "Thông báo",
	                JOptionPane.WARNING_MESSAGE);
	        return;
	    }

	    // Bước 1: Nhập thông tin khách hàng
	    JTextField soDienThoaiField = new JTextField(15);
	    JTextField tenField = new JTextField(15);
	    JTextField sinhNhatField = new JTextField(15);
	    JTextField emailField = new JTextField(15); // Thêm textfield cho email
	    JButton searchButton = new JButton("Tìm kiếm");

	    JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10)); // Tăng số hàng lên 5
	    inputPanel.setBackground(new Color(245, 245, 245));
	    inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	    JLabel soDienThoaiLabel = new JLabel("Số điện thoại:");
	    soDienThoaiLabel.setFont(new Font("Arial", Font.BOLD, 14));
	    soDienThoaiField.setBackground(new Color(230, 243, 250));
	    soDienThoaiField.setBorder(BorderFactory.createLineBorder(new Color(0, 120, 215), 1));
	    JPanel phonePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    phonePanel.setBackground(new Color(245, 245, 245));
	    phonePanel.add(soDienThoaiField);
	    searchButton.setBackground(new Color(74, 144, 226));
	    searchButton.setForeground(Color.WHITE);
	    searchButton.setFocusPainted(false);
	    searchButton.setFont(new Font("Arial", Font.BOLD, 12));
	    searchButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
	    searchButton.addActionListener(e -> {
	        String soDienThoai = soDienThoaiField.getText().trim();
	        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
	            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
	        } else {
	            try {
	                KhachHang khachHang = khachHangDAO.getKhachHangBySoDienThoai(soDienThoai);
	                if (khachHang != null) {
	                    tenField.setText(khachHang.getTen());
	                    sinhNhatField.setText(khachHang.getSinhNhat() != null ? khachHang.getSinhNhat().toString() : "");
	                    emailField.setText(khachHang.getEmail() != null ? khachHang.getEmail() : ""); // Điền email
	                } else {
	                    tenField.setText("");
	                    sinhNhatField.setText("");
	                    emailField.setText(""); // Reset email khi không tìm thấy
	                }
	            } catch (SQLException ex) {
	                ex.printStackTrace();
	                JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm: " + ex.getMessage(), "Lỗi",
	                        JOptionPane.ERROR_MESSAGE);
	            }
	        }
	    });
	    phonePanel.add(searchButton);

	    JLabel tenLabel = new JLabel("Tên:");
	    tenLabel.setFont(new Font("Arial", Font.BOLD, 14));
	    tenField.setBackground(new Color(230, 243, 250));
	    tenField.setBorder(BorderFactory.createLineBorder(new Color(0, 120, 215), 1));

	    JLabel sinhNhatLabel = new JLabel("Sinh nhật (YYYY-MM-DD):");
	    sinhNhatLabel.setFont(new Font("Arial", Font.BOLD, 14));
	    sinhNhatField.setBackground(new Color(230, 243, 250));
	    sinhNhatField.setBorder(BorderFactory.createLineBorder(new Color(0, 120, 215), 1));

	    JLabel emailLabel = new JLabel("Email:"); // Thêm label cho email
	    emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
	    emailField.setBackground(new Color(230, 243, 250));
	    emailField.setBorder(BorderFactory.createLineBorder(new Color(0, 120, 215), 1));

	    inputPanel.add(soDienThoaiLabel);
	    inputPanel.add(phonePanel);
	    inputPanel.add(tenLabel);
	    inputPanel.add(tenField);
	    inputPanel.add(sinhNhatLabel);
	    inputPanel.add(sinhNhatField);
	    inputPanel.add(emailLabel); // Thêm label email
	    inputPanel.add(emailField); // Thêm textfield email

	    JOptionPane pane = new JOptionPane(inputPanel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
	    JDialog dialog = pane.createDialog(this, "Nhập thông tin khách hàng");
	    dialog.getContentPane().setBackground(new Color(245, 245, 245));
	    JRootPane rootPane = dialog.getRootPane();
	    JButton okButton = rootPane.getDefaultButton();
	    okButton.setBackground(new Color(74, 144, 226));
	    okButton.setForeground(Color.WHITE);
	    okButton.setFocusPainted(false);
	    okButton.setFont(new Font("Arial", Font.BOLD, 12));
	    okButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
	    JButton cancelButton = (JButton) Arrays.stream(rootPane.getComponents())
	            .filter(c -> c instanceof JButton && ((JButton) c).getText().equals("Cancel")).findFirst().orElse(null);
	    if (cancelButton != null) {
	        cancelButton.setBackground(new Color(74, 144, 226));
	        cancelButton.setForeground(Color.WHITE);
	        cancelButton.setFocusPainted(false);
	        cancelButton.setFont(new Font("Arial", Font.BOLD, 12));
	        cancelButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
	    }
	    dialog.setVisible(true);

	    if (pane.getValue() != null && (int) pane.getValue() == JOptionPane.OK_OPTION) {
	        String soDienThoai = soDienThoaiField.getText().trim();
	        String ten = tenField.getText().trim();
	        String sinhNhatStr = sinhNhatField.getText().trim();
	        String email = emailField.getText().trim(); // Lấy giá trị email
	        LocalDate sinhNhat = (sinhNhatStr.isEmpty()) ? LocalDate.of(2000, 1, 1) : LocalDate.parse(sinhNhatStr);
	        int idKhachHang = -1;
	        float uuDai = 0;

	        try {
	            KhachHang existingKhachHang = khachHangDAO.getKhachHangBySoDienThoai(soDienThoai);
	            if (existingKhachHang != null) {
	                idKhachHang = existingKhachHang.getId();
	            } else {
	                KhachHang newKhachHang = new KhachHang(0, ten, soDienThoai, 0, "Thanh Vien", sinhNhat, email); // Thêm email
	                idKhachHang = khachHangDAO.addKhachHang(newKhachHang);
	            }

	            KhachHang khachHang = khachHangDAO.getKhachHangById(idKhachHang);
	            XepHangKhachHangDAO xepHangDAO = new XepHangKhachHangDAO();
	            XepHangKhachHang xepHang = xepHangDAO.getXepHangByDiem(khachHang.getDiemTichLuy());
	            if (xepHang != null) {
	                khachHang.setCapBac(xepHang.getCapBac());
	                uuDai = xepHang.getUuDai();
	                khachHang.setEmail(email); // Cập nhật email
	                khachHangDAO.updateKhachHang(khachHang);
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	            JOptionPane.showMessageDialog(this, "Lỗi khi xử lý khách hàng: " + e.getMessage(), "Lỗi",
	                    JOptionPane.ERROR_MESSAGE);
	            return;
	        }

	        // Bước 2: Tạo bill
	        Map<Integer, Integer> monAnMap = new HashMap<>();
	        long tongTien = 0;
	        try {
	            List<ChiTietOrder> chiTietOrders = chiTietOrderDAO.getChiTietOrdersByOrderId(currentOrderId);
	            for (ChiTietOrder chiTiet : chiTietOrders) {
	                monAnMap.merge(chiTiet.getMonAnId(), chiTiet.getSoLuong(), Integer::sum);
	                MonAn monAn = monAnDAO.getMonAnById(chiTiet.getMonAnId());
	                if (monAn != null) {
	                    tongTien += monAn.getGia() * chiTiet.getSoLuong();
	                }
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	            JOptionPane.showMessageDialog(this, "Lỗi khi lấy chi tiết đơn hàng: " + e.getMessage(), "Lỗi",
	                    JOptionPane.ERROR_MESSAGE);
	            return;
	        }

	        long tongTienSauDiscount = (long) (tongTien * (1 - uuDai));
	        System.out.println("tongTien: " + tongTien + ", uuDai: " + uuDai + ", tongTienSauDiscount: " + tongTienSauDiscount);

	        JPanel paymentPanel = new JPanel(new GridLayout(8, 1, 10, 10));
	        paymentPanel.setBackground(new Color(245, 245, 245));
	        paymentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	        JLabel danhsachMonLabel = new JLabel("Danh sách món:");
	        danhsachMonLabel.setFont(new Font("Arial", Font.BOLD, 14));
	        JTextArea billArea = new JTextArea();
	        billArea.setEditable(false);
	        billArea.setBackground(new Color(230, 243, 250));
	        billArea.setBorder(BorderFactory.createLineBorder(new Color(0, 120, 215), 1));
	        for (Map.Entry<Integer, Integer> entry : monAnMap.entrySet()) {
	            MonAn monAn = monAnDAO.getMonAnById(entry.getKey());
	            if (monAn != null) {
	                billArea.append(monAn.getTenMon() + " x" + entry.getValue() + "\n");
	            }
	        }
	        JScrollPane scrollPane = new JScrollPane(billArea);
	        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0, 120, 215), 1));

	        JLabel tongTienLabel = new JLabel("Tổng tiền: " + new DecimalFormat("#,### VNĐ").format(tongTien));
	        tongTienLabel.setFont(new Font("Arial", Font.BOLD, 14));
	        JLabel discountLabel = new JLabel("Discount: " + (uuDai * 100) + "%");
	        discountLabel.setFont(new Font("Arial", Font.BOLD, 14));
	        JLabel tongSauDiscountLabel = new JLabel("Tổng sau discount: " + new DecimalFormat("#,### VNĐ").format(tongTienSauDiscount));
	        tongSauDiscountLabel.setFont(new Font("Arial", Font.BOLD, 14));

	        JCheckBox codCheckBox = new JCheckBox("COD");
	        codCheckBox.setBackground(new Color(245, 245, 245));
	        codCheckBox.setBorder(BorderFactory.createLineBorder(new Color(0, 120, 215), 1));
	        codCheckBox.setFont(new Font("Arial", Font.BOLD, 12));
	        JCheckBox vnPayCheckBox = new JCheckBox("VNPay");
	        vnPayCheckBox.setBackground(new Color(245, 245, 245));
	        vnPayCheckBox.setBorder(BorderFactory.createLineBorder(new Color(0, 120, 215), 1));
	        vnPayCheckBox.setFont(new Font("Arial", Font.BOLD, 12));

	        paymentPanel.add(danhsachMonLabel);
	        paymentPanel.add(scrollPane);
	        paymentPanel.add(tongTienLabel);
	        paymentPanel.add(discountLabel);
	        paymentPanel.add(tongSauDiscountLabel);
	        paymentPanel.add(codCheckBox);
	        paymentPanel.add(vnPayCheckBox);

	        JOptionPane pane1 = new JOptionPane(paymentPanel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
	        JDialog dialog1 = pane1.createDialog(this, "Thanh toán");
	        dialog1.getContentPane().setBackground(new Color(245, 245, 245));
	        JRootPane rootPane1 = dialog1.getRootPane();
	        JButton okButton1 = rootPane1.getDefaultButton();
	        okButton1.setBackground(new Color(74, 144, 226));
	        okButton1.setForeground(Color.WHITE);
	        okButton1.setFocusPainted(false);
	        okButton1.setFont(new Font("Arial", Font.BOLD, 12));
	        okButton1.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
	        JButton cancelButton1 = (JButton) Arrays.stream(rootPane1.getComponents())
	                .filter(c -> c instanceof JButton && ((JButton) c).getText().equals("Cancel")).findFirst()
	                .orElse(null);
	        if (cancelButton1 != null) {
	            cancelButton1.setBackground(new Color(74, 144, 226));
	            cancelButton1.setForeground(Color.WHITE);
	            cancelButton1.setFocusPainted(false);
	            cancelButton1.setFont(new Font("Arial", Font.BOLD, 12));
	            cancelButton1.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
	        }
	        dialog1.setVisible(true);

	        int paymentResult = (pane1.getValue() != null) ? (int) pane1.getValue() : JOptionPane.CANCEL_OPTION;
	        if (paymentResult != JOptionPane.OK_OPTION) {
	            return;
	        }

	        String phuongThucThanhToan = codCheckBox.isSelected() ? "COD" : vnPayCheckBox.isSelected() ? "VNPay" : null;
	        if (phuongThucThanhToan == null) {
	            JOptionPane.showMessageDialog(this, "Vui lòng chọn phương thức thanh toán!", "Thông báo",
	                    JOptionPane.WARNING_MESSAGE);
	            return;
	        }

	        // Bước 3: Xử lý thanh toán
	        try {
	            if ("COD".equals(phuongThucThanhToan)) {
	                // Lấy ID hóa đơn vừa tạo
	                HoaDon hoaDon = new HoaDon(0, currentOrderId, tongTienSauDiscount, LocalDateTime.now(), idKhachHang,
	                        phuongThucThanhToan);
	                int idHoaDon = hoaDonDAO.addHoaDon(hoaDon);

	                // Thêm chi tiết hóa đơn
	                List<ChiTietOrder> chiTietOrders = chiTietOrderDAO.getChiTietOrdersByOrderId(currentOrderId);
	                System.out.println("Số lượng chi tiết đơn hàng: " + chiTietOrders.size());
	                for (ChiTietOrder chiTiet : chiTietOrders) {
	                    MonAn monAn = monAnDAO.getMonAnById(chiTiet.getMonAnId());
	                    if (monAn != null) {
	                        ChiTietHoaDon chiTietHoaDon = new ChiTietHoaDon(0, 
	                                idHoaDon, chiTiet.getMonAnId(), chiTiet.getSoLuong(),
	                                monAn.getGia() * chiTiet.getSoLuong());
	                        System.out.println("chi tiết hóa đơn: " + chiTietHoaDon);
	                        chiTietHoaDonDAO.addChiTietHoaDon(chiTietHoaDon);
	                    }
	                }
	                KhachHang khachHang = khachHangDAO.getKhachHangById(idKhachHang);
	                int diemTichLuyTang = (int) (tongTien / 100000);
	                System.out.println("Điểm cũ:" + khachHang.getDiemTichLuy());
	                int diemTichLuyMoi = khachHang.getDiemTichLuy() + diemTichLuyTang;
	                System.out.println("Điểm tăng thêm:" + diemTichLuyTang);
	                System.out.println("Điểm mới:" + diemTichLuyMoi);
	                khachHang.setDiemTichLuy(diemTichLuyMoi);
	                khachHangDAO.updateKhachHang(khachHang);

	                int diemThuongCu = luongDAO.getDiemThuongByIdNhanVien(idTaiKhoan);
	                System.out.println("Điểm thưởng cũ: " + diemThuongCu);
	                int diemThuong = (int) (tongTien / 100000);
	                System.out.println("Điểm thưởng: " + diemThuong);
	                int diemThuongMoi = diemThuongCu + diemThuong;
	                System.out.println("Điểm thưởng mới: " + diemThuongMoi);
	                luongDAO.updateDiemThuong(idTaiKhoan, diemThuongMoi);

	                updateBanAnStatus("Trong");
	                orderDAO.updateOrderStatus(currentOrderId, "DaThanhToan");

	                JOptionPane.showMessageDialog(this, "Thanh toán thành công cho Bàn " + idBan + "!", "Thông báo",
	                        JOptionPane.INFORMATION_MESSAGE);
	                gioHangList.clear();
	                gioHangModel.setRowCount(0);
	                currentOrderId = -1;
	                updateGioHangDisplay();
	                dispose();
	                new ChaoMungUI(idTaiKhoan, tenNhanVien);
	            } else if ("VNPay".equals(phuongThucThanhToan)) {
	                long amount = tongTienSauDiscount;
	                String vnp_TxnRef = String.format("%06d", currentOrderId); // Tạo vnp_TxnRef từ currentOrderId
	                String vnp_OrderInfo = "Thanh toan don hang: " + vnp_TxnRef;

	                try {
	                    String paymentUrl = ajaxServlet.generatePaymentUrl(amount, vnp_OrderInfo, "vn", vnp_TxnRef);
	                    System.out.println("Generated VNPay URL: " + paymentUrl);

	                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
	                        Desktop.getDesktop().browse(new URI(paymentUrl));
	                        JOptionPane.showMessageDialog(this,
	                                "Vui lòng hoàn tất thanh toán trên trang VNPay. Ứng dụng sẽ tự động cập nhật sau khi thành công!",
	                                "Thông báo", JOptionPane.INFORMATION_MESSAGE);

	                        final long finalTongTien = tongTien;
	                        final long finalTongTienSauDiscount = tongTienSauDiscount;
	                        final int finalIdKhachHang = idKhachHang;
	                        final String finalVnp_TxnRef = vnp_TxnRef;

	                        new Thread(() -> {
	                            watchForVnpaySuccess(finalTongTien, finalTongTienSauDiscount, finalIdKhachHang,
	                                    finalVnp_TxnRef);
	                        }).start();
	                    } else {
	                        JOptionPane.showMessageDialog(this, "Không thể mở trình duyệt để thanh toán VNPay!", "Lỗi",
	                                JOptionPane.ERROR_MESSAGE);
	                    }
	                } catch (UnsupportedEncodingException | URISyntaxException e) {
	                    e.printStackTrace();
	                    JOptionPane.showMessageDialog(this, "Lỗi khi tạo URL thanh toán VNPay!", "Lỗi",
	                            JOptionPane.ERROR_MESSAGE);
	                }
	            }
	        } catch (SQLException e) {
	            JOptionPane.showMessageDialog(this, "Lỗi khi thanh toán: " + e.getMessage(), "Lỗi",
	                    JOptionPane.ERROR_MESSAGE);
	            e.printStackTrace();
	        } catch (Exception e) {
	            JOptionPane.showMessageDialog(this, "Lỗi khi tạo thanh toán VNPay: " + e.getMessage(), "Lỗi",
	                    JOptionPane.ERROR_MESSAGE);
	            e.printStackTrace();
	        }
	    }
	}

	private void watchForVnpaySuccess(long tongTien, long tongTienSauDiscount, int idKhachHang, String vnp_TxnRef) {
		final int MAX_ATTEMPTS = 10;
		final int POLL_INTERVAL = 2000;
		int attempts = 0;

		while (attempts < MAX_ATTEMPTS) {
			try {
				Thread.sleep(POLL_INTERVAL);
				JsonObject queryResult = vnpayQuery.queryTransaction(vnp_TxnRef);
				System.out.println("Query Result (Attempt " + (attempts + 1) + "): " + queryResult.toString());

				String transactionStatus = queryResult.has("vnp_TransactionStatus")
						? queryResult.get("vnp_TransactionStatus").getAsString()
						: null;

				if ("00".equals(transactionStatus)) {
					SwingUtilities.invokeLater(() -> {
						try {
							// Lấy ID hóa đơn vừa tạo
							HoaDon hoaDon = new HoaDon(0, currentOrderId, tongTienSauDiscount, LocalDateTime.now(),
									idKhachHang, "VNPay");
							int idHoaDon = hoaDonDAO.addHoaDon(hoaDon);

							// Thêm chi tiết hóa đơn
							List<ChiTietOrder> chiTietOrders = chiTietOrderDAO
									.getChiTietOrdersByOrderId(currentOrderId);
							for (ChiTietOrder chiTiet : chiTietOrders) {
								MonAn monAn = monAnDAO.getMonAnById(chiTiet.getMonAnId());
								if (monAn != null) {
									ChiTietHoaDon chiTietHoaDon = new ChiTietHoaDon(0, idHoaDon, chiTiet.getMonAnId(),
											chiTiet.getSoLuong(), monAn.getGia() * chiTiet.getSoLuong());
									System.out.println("chi tiết hóa đơn:" + chiTietHoaDon);
									chiTietHoaDonDAO.addChiTietHoaDon(chiTietHoaDon);
								}
							}

							KhachHang khachHang = khachHangDAO.getKhachHangById(idKhachHang);
							int diemTichLuyTang = (int) (tongTien / 100000);
							int diemTichLuyMoi = khachHang.getDiemTichLuy() + diemTichLuyTang;
							khachHang.setDiemTichLuy(diemTichLuyMoi);
							khachHangDAO.updateKhachHang(khachHang);

							int diemThuongCu = luongDAO.getDiemThuongByIdNhanVien(idTaiKhoan);
							int diemThuong = (int) (tongTien / 100000);
							int diemThuongMoi = diemThuongCu + diemThuong;
							luongDAO.updateDiemThuong(idTaiKhoan, diemThuongMoi);

							updateBanAnStatus("Trong");
							orderDAO.updateOrderStatus(currentOrderId, "DaThanhToan");

							JOptionPane.showMessageDialog(this, "Thanh toán VNPay thành công cho Bàn " + idBan + "!",
									"Thông báo", JOptionPane.INFORMATION_MESSAGE);
							gioHangList.clear();
							gioHangModel.setRowCount(0);
							currentOrderId = -1;
							updateGioHangDisplay();
							dispose();
							new ChaoMungUI(idTaiKhoan, tenNhanVien);
						} catch (SQLException e) {
							e.printStackTrace();
							JOptionPane.showMessageDialog(this,
									"Lỗi khi lưu thông tin thanh toán! Vui lòng liên hệ hỗ trợ.", "Lỗi",
									JOptionPane.ERROR_MESSAGE);
						}
					});
					return;
				} else if (transactionStatus != null) {
					attempts++;
					if (attempts >= MAX_ATTEMPTS) {
						SwingUtilities.invokeLater(() -> {
							JOptionPane.showMessageDialog(this,
									"Thanh toán thất bại! Vui lòng thử lại hoặc liên hệ hỗ trợ.", "Thất bại",
									JOptionPane.ERROR_MESSAGE);
						});
						return;
					}
				}
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
				SwingUtilities.invokeLater(() -> {
					JOptionPane.showMessageDialog(this, "Lỗi khi kiểm tra trạng thái! Vui lòng thử lại.", "Lỗi",
							JOptionPane.ERROR_MESSAGE);
				});
				return;
			}
		}
	}

	private void clearCart() {
		gioHangList.clear();
		gioHangModel.setRowCount(0);
		updateGioHangDisplay();
	}

	private class ImageRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			JLabel label = new JLabel();
			if (value instanceof ImageIcon) {
				label.setIcon((ImageIcon) value);
			} else {
				label.setText("No Image");
				label.setFont(new Font("Roboto", Font.ITALIC, 12));
				label.setForeground(new Color(150, 150, 150));
			}
			label.setHorizontalAlignment(JLabel.CENTER);
			label.setOpaque(false);
			return label;
		}
	}

	private class ButtonRenderer extends JPanel implements TableCellRenderer {
		private JButton minusButton;
		private JButton plusButton;
		private JButton deleteButton;

		public ButtonRenderer() {
			setLayout(new GridBagLayout());
			setOpaque(false);

			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(0, 5, 0, 5);
			gbc.anchor = GridBagConstraints.CENTER;

			minusButton = createStyledButton("−", new Color(255, 140, 0));
			gbc.gridx = 0;
			add(minusButton, gbc);

			plusButton = createStyledButton("+", new Color(0, 128, 0));
			gbc.gridx = 1;
			add(plusButton, gbc);

			deleteButton = createStyledButton("✕", new Color(220, 53, 69));
			gbc.gridx = 2;
			add(deleteButton, gbc);
		}

		private JButton createStyledButton(String text, Color bgColor) {
			JButton button = new JButton(text);
			button.setFont(new Font("Roboto", Font.BOLD, 14));
			button.setForeground(Color.WHITE);
			button.setBackground(bgColor);
			button.setFocusPainted(false);
			button.setContentAreaFilled(true);
			button.setOpaque(true);
			button.setBorder(BorderFactory.createLineBorder(bgColor.darker(), 2, true));
			button.setPreferredSize(new Dimension(34, 34));
			button.setCursor(new Cursor(Cursor.HAND_CURSOR));
			button.putClientProperty("JButton.buttonType", "square");

			button.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					button.setBackground(bgColor.brighter());
				}

				@Override
				public void mouseExited(MouseEvent e) {
					button.setBackground(bgColor);
				}
			});

			return button;
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			return this;
		}
	}

	private class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
		private JPanel panel;
		private JButton minusButton;
		private JButton plusButton;
		private JButton deleteButton;
		private int currentRow = -1;

		public ButtonEditor() {
			panel = new JPanel();
			panel.setLayout(new GridBagLayout());
			panel.setOpaque(false);

			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(0, 5, 0, 5);
			gbc.anchor = GridBagConstraints.CENTER;

			minusButton = createStyledButton("−", new Color(255, 140, 0));
			gbc.gridx = 0;
			panel.add(minusButton, gbc);

			plusButton = createStyledButton("+", new Color(0, 128, 0));
			plusButton.setContentAreaFilled(true);
			plusButton.setOpaque(true);
			gbc.gridx = 1;
			panel.add(plusButton, gbc);

			deleteButton = createStyledButton("✕", new Color(220, 53, 69));
			deleteButton.setContentAreaFilled(true);
			deleteButton.setOpaque(true);
			gbc.gridx = 2;
			panel.add(deleteButton, gbc);

			minusButton.addActionListener(e -> adjustQuantity(-1));
			plusButton.addActionListener(e -> adjustQuantity(1));
			deleteButton.addActionListener(e -> deleteItem());
		}

		private JButton createStyledButton(String text, Color bgColor) {
			JButton button = new JButton(text);
			button.setFont(new Font("Roboto", Font.BOLD, 12));
			button.setBackground(bgColor);
			button.setForeground(Color.WHITE);
			button.setFocusPainted(false);
			button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
			button.setCursor(new Cursor(Cursor.HAND_CURSOR));
			button.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					button.setBackground(bgColor.brighter());
				}

				@Override
				public void mouseExited(MouseEvent e) {
					button.setBackground(bgColor);
				}
			});
			return button;
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
			currentRow = row;
			return panel;
		}

		@Override
		public Object getCellEditorValue() {
			return null;
		}

		private void adjustQuantity(int delta) {
			if (currentRow >= 0 && currentRow < gioHangList.size()) {
				GioHangItem item = gioHangList.get(currentRow);
				int newQuantity = item.getSoLuong() + delta;
				if (newQuantity > 0) {
					item.setSoLuong(newQuantity);
				} else {
					gioHangList.remove(currentRow);
				}
				updateGioHangDisplay();
			}
			fireEditingStopped();
		}

		private void deleteItem() {
			if (currentRow >= 0 && currentRow < gioHangList.size()) {
				gioHangList.remove(currentRow);
				updateGioHangDisplay();
				// Reset currentRow and stop editing to prevent invalid access
				currentRow = -1;
				fireEditingStopped();
			}
		}

		@Override
		public boolean isCellEditable(EventObject e) {
			return true;
		}
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		SwingUtilities.invokeLater(() -> new GiaoDienChinhUI(1, "Nhật Nguyễn", 4));
	}
}