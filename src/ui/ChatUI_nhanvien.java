package ui;

import utils.FileEncryption;
import utils.ImageSteganography;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import dao.NhanVienDAO;
import model.NhanVien;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ChatUI_nhanvien extends JFrame {
    private JTextPane chatArea;
    private JTextField messageField;
    private DataOutputStream out;
    private DataInputStream in;
    private Socket socket;
    private static final String TEMP_DIR = "ClientTemp/";
    private Map<String, File> pendingFiles = new HashMap<>();
    private JButton reloadButton;
    private JButton emojiButton;
    private JButton recordButton;
    private boolean isRecording = false;
    private AudioFormat audioFormat;
    private TargetDataLine targetDataLine;
    private File audioFile;
    private int idTaiKhoan;
    private NhanVienDAO nhanVienDAO;
    private String tenNhanVien;

    public ChatUI_nhanvien(int idTaiKhoan) throws SQLException {
        this.nhanVienDAO = new NhanVienDAO();
        this.idTaiKhoan = idTaiKhoan;
        NhanVien nhanVien = nhanVienDAO.getNhanVienByIdTaiKhoan(idTaiKhoan);
        this.tenNhanVien = (nhanVien != null) ? nhanVien.getTen() : "Nhân viên chưa xác định";

        Locale.setDefault(new Locale("vi", "VN"));
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("2MN-Chat Box");
        setSize(732, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(new Color(240, 242, 245));
        getContentPane().setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Xin chào " + this.tenNhanVien, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 123, 255));
        titleLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        getContentPane().add(titleLabel, BorderLayout.NORTH);

        chatArea = new JTextPane();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 14));
        chatArea.setBackground(Color.WHITE);
        chatArea.setForeground(Color.BLACK);
        chatArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(new Color(240, 242, 245));
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 0));
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        bottomPanel.setBackground(new Color(240, 242, 245));

        messageField = new JTextField();
        messageField.setFont(new Font("Arial", Font.PLAIN, 14));
        messageField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        bottomPanel.add(messageField, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(new Color(240, 242, 245));

        emojiButton = new JButton("\uD83D\uDE04");
        emojiButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        emojiButton.setBackground(new Color(255, 193, 7));
        emojiButton.setForeground(Color.WHITE);
        emojiButton.setFocusPainted(false);
        emojiButton.addActionListener(e -> showEmojiPicker());
        buttonPanel.add(emojiButton);

        JButton sendButton = new JButton("Gửi tin nhắn");
        sendButton.setFont(new Font("Arial", Font.BOLD, 14));
        sendButton.setBackground(new Color(0, 123, 255));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        sendButton.addActionListener(e -> sendMessage());
        buttonPanel.add(sendButton);

        JButton sendFileButton = new JButton("Gửi tệp");
        sendFileButton.setFont(new Font("Arial", Font.BOLD, 14));
        sendFileButton.setBackground(new Color(40, 167, 69));
        sendFileButton.setForeground(Color.WHITE);
        sendFileButton.setFocusPainted(false);
        sendFileButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        sendFileButton.addActionListener(e -> sendFile());
        buttonPanel.add(sendFileButton);

        JButton sendImageButton = new JButton("Gửi ảnh");
        sendImageButton.setFont(new Font("Arial", Font.BOLD, 14));
        sendImageButton.setBackground(new Color(255, 87, 34));
        sendImageButton.setForeground(Color.WHITE);
        sendImageButton.setFocusPainted(false);
        sendImageButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        sendImageButton.addActionListener(e -> sendImageWithOption());
        buttonPanel.add(sendImageButton);

        recordButton = new JButton("Ghi âm");
        recordButton.setFont(new Font("Arial", Font.BOLD, 14));
        recordButton.setBackground(new Color(0, 123, 255));
        recordButton.setForeground(Color.WHITE);
        recordButton.setFocusPainted(false);
        recordButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        recordButton.addActionListener(e -> toggleRecording());
        buttonPanel.add(recordButton);

        reloadButton = new JButton("Tải lại tệp");
        reloadButton.setFont(new Font("Arial", Font.BOLD, 14));
        reloadButton.setBackground(new Color(108, 117, 125));
        reloadButton.setForeground(Color.WHITE);
        reloadButton.setFocusPainted(false);
        reloadButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        reloadButton.setEnabled(false);
        reloadButton.addActionListener(e -> reloadFile());
        buttonPanel.add(reloadButton);

        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        new File(TEMP_DIR).mkdirs();
        connectToServer();
        setVisible(true);
    }

    private void appendToChat(String sender, String message, boolean isSelf) {
        try {
            StyledDocument doc = chatArea.getStyledDocument();
            Style style = chatArea.addStyle("ChatStyle", null);
            StyleConstants.setAlignment(style, isSelf ? StyleConstants.ALIGN_RIGHT : StyleConstants.ALIGN_LEFT);
            StyleConstants.setForeground(style, isSelf ? new Color(0, 123, 255) : Color.BLACK);

            String displaySender = sender.equals(this.tenNhanVien) ? sender : sender;
            if (!message.startsWith("gửi tệp: ") && !message.startsWith("Tệp lịch sử: ") && !message.startsWith("gửi ảnh: ")) {
                doc.insertString(doc.getLength(), displaySender + ": " + message + "\n", style);
                chatArea.setCaretPosition(doc.getLength());
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void appendAudioToChat(String audioName, File audioFile) {
        try {
            StyledDocument doc = chatArea.getStyledDocument();
            Style style = chatArea.addStyle("AudioStyle_" + audioName, null);

            JButton playButton = new JButton("Phát: " + audioName);
            playButton.setFont(new Font("Arial", Font.PLAIN, 14));
            playButton.setBackground(new Color(0, 123, 255));
            playButton.setForeground(Color.WHITE);
            playButton.setFocusPainted(false);
            playButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            playButton.addActionListener(e -> playAudio(audioFile));

            StyleConstants.setComponent(style, playButton);
            doc.insertString(doc.getLength(), "ignored text", style);
            doc.insertString(doc.getLength(), "\n", null);
            chatArea.setCaretPosition(doc.getLength());

            appendToChat("Hệ thống", "Nhận được tệp ghi âm: " + audioName, false);
        } catch (BadLocationException e) {
            appendToChat("Hệ thống", "Lỗi khi hiển thị tệp ghi âm: " + e.getMessage(), false);
        }
    }

    private void playAudio(File audioFile) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();

            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                    try {
                        audioInputStream.close();
                    } catch (IOException e) {
                        appendToChat("Hệ thống", "Lỗi khi đóng luồng âm thanh: " + e.getMessage(), false);
                    }
                }
            });
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            appendToChat("Hệ thống", "Lỗi khi phát tệp ghi âm: " + e.getMessage(), false);
        }
    }

    private void connectToServer() {
        try {
            socket = new Socket("127.0.0.1", 12345);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());

            out.writeUTF(this.tenNhanVien);
            out.flush();

            int historySize = in.readInt();
            for (int i = 0; i < historySize; i++) {
                String type = in.readUTF();
                String sender = in.readUTF();
                String content = in.readUTF();
                switch (type) {
                    case "TEXT":
                        appendToChat(sender, content, sender.equals(this.tenNhanVien));
                        break;
                    case "FILE":
                        long fileSize = in.readLong();
                        receiveFile(sender, content, fileSize, true);
                        break;
                    case "IMAGE":
                        long imageSize = in.readLong();
                        receiveImage(sender, content, imageSize, true);
                        break;
                    case "AUDIO":
                        long audioSize = in.readLong();
                        receiveAudio(sender, content, audioSize, true);
                        break;
                }
            }

            new Thread(() -> {
                try {
                    while (true) {
                        String type = in.readUTF();
                        switch (type) {
                            case "TEXT":
                                String message = in.readUTF();
                                String sender = message.split(": ")[0];
                                String msgContent = message.substring(message.indexOf(": ") + 2);
                                appendToChat(sender, msgContent, sender.equals(this.tenNhanVien));
                                break;
                            case "FILE":
                                String fileName = in.readUTF();
                                long fileSize = in.readLong();
                                receiveFile(fileName, fileName, fileSize, false);
                                break;
                            case "IMAGE":
                                String imageName = in.readUTF();
                                long imageSize = in.readLong();
                                receiveImage(imageName, imageName, imageSize, false);
                                break;
                            case "AUDIO":
                                String audioName = in.readUTF();
                                long audioSize = in.readLong();
                                receiveAudio(audioName, audioName, audioSize, false);
                                break;
                            default:
                                appendToChat("Hệ thống", "Loại dữ liệu không xác định: " + type, false);
                                break;
                        }
                    }
                } catch (IOException e) {
                    appendToChat("Hệ thống", "Mất kết nối với server: " + e.getMessage(), false);
                }
            }).start();

        } catch (IOException e) {
            appendToChat("Hệ thống", "Lỗi kết nối server: " + e.getMessage(), false);
        }
    }

    private void sendMessage() {
        try {
            String message = messageField.getText().trim();
            if (!message.isEmpty()) {
                out.writeUTF("TEXT");
                out.writeUTF(this.tenNhanVien + ": " + message);
                out.flush();
                appendToChat(this.tenNhanVien, message, true);
                messageField.setText("");
            }
        } catch (IOException e) {
            appendToChat("Hệ thống", "Lỗi khi gửi tin nhắn: " + e.getMessage(), false);
        }
    }

    private void sendFile() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = fileChooser.getSelectedFile();
        String password = JOptionPane.showInputDialog(this, "Nhập mật khẩu để mã hóa tệp:");
        if (password == null || password.trim().isEmpty()) {
            appendToChat("Hệ thống", "Mật khẩu không hợp lệ, hủy gửi tệp.", false);
            return;
        }

        try {
            File encryptedFile = new File(TEMP_DIR + "temp_encrypted_" + file.getName());
            FileEncryption.encryptFile(password, file, encryptedFile);

            out.writeUTF("FILE");
            out.writeUTF(file.getName());
            out.writeLong(encryptedFile.length());

            FileInputStream fis = new FileInputStream(encryptedFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
            fis.close();

            appendToChat(this.tenNhanVien, "Đã gửi tệp: " + file.getName() + " (Mã hóa với mật khẩu)", true);
            encryptedFile.delete();
        } catch (Exception e) {
            appendToChat("Hệ thống", "Lỗi khi gửi tệp: " + e.getMessage(), false);
        }
    }

    private void sendImageWithOption() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif"));
        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File originalImage = fileChooser.getSelectedFile();
        File imageToSend = originalImage;

        int choice = JOptionPane.showConfirmDialog(this, "Bạn có muốn giấu thông tin trong ảnh trước khi gửi không?", "Giấu tin nhắn", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            String secretMessage = JOptionPane.showInputDialog(this, "Nhập thông điệp cần giấu:");
            String password = JOptionPane.showInputDialog(this, "Nhập mã bảo vệ:");
            if (secretMessage == null || secretMessage.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                appendToChat("Hệ thống", "Không hợp lệ. Hủy gửi ảnh.", false);
                return;
            }

            try {
                File encodedImage = new File(TEMP_DIR + "encoded_" + originalImage.getName());
                ImageSteganography.hideMessage(originalImage.getAbsolutePath(), encodedImage.getAbsolutePath(), secretMessage, password);
                imageToSend = encodedImage;
                appendToChat("Hệ thống", "Đã giấu tin nhắn trong ảnh: " + encodedImage.getName(), true);
            } catch (Exception e) {
                appendToChat("Hệ thống", "Lỗi giấu tin: " + e.getMessage(), false);
                return;
            }
        }

        try {
            out.writeUTF("IMAGE");
            out.writeUTF(imageToSend.getName());
            out.writeLong(imageToSend.length());

            FileInputStream fis = new FileInputStream(imageToSend);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
            fis.close();

            showImageInChat(imageToSend);

            if (!imageToSend.equals(originalImage)) {
                imageToSend.delete();
            }
        } catch (IOException e) {
            appendToChat("Hệ thống", "Lỗi gửi ảnh: " + e.getMessage(), false);
        }
    }

    private void showImageInChat(File imageFile) {
        try {
            ImageIcon imageIcon = new ImageIcon(imageFile.getAbsolutePath());
            Image scaledImage = imageIcon.getImage().getScaledInstance(200, -1, Image.SCALE_SMOOTH);
            imageIcon = new ImageIcon(scaledImage);

            StyledDocument doc = chatArea.getStyledDocument();
            Style style = chatArea.addStyle("ImageStyle", null);
            StyleConstants.setIcon(style, imageIcon);

            doc.insertString(doc.getLength(), "ignored text", style);
            doc.insertString(doc.getLength(), "\n", null);
            chatArea.setCaretPosition(doc.getLength());
        } catch (Exception e) {
            appendToChat("Hệ thống", "Lỗi hiển thị ảnh: " + e.getMessage(), false);
        }
    }

    private void showEmojiPicker() {
        JDialog emojiDialog = new JDialog(this, "Chọn Emoji", true);
        emojiDialog.getContentPane().setLayout(new GridLayout(4, 5, 5, 5));
        emojiDialog.setSize(300, 200);
        emojiDialog.setLocationRelativeTo(this);

        String[] emojis = {
                "\uD83D\uDE04", "\uD83D\uDE0A", "\uD83D\uDE03", "\uD83D\uDE09", "\uD83D\uDE06",
                "\uD83D\uDE2D", "\uD83D\uDE22", "\uD83D\uDE1E", "\uD83D\uDE14", "\uD83D\uDE12",
                "\uD83D\uDE0D", "\uD83D\uDE18", "\uD83D\uDE17", "\uD83D\uDE19", "\uD83D\uDE1A",
                "\uD83D\uDC4D", "\uD83D\uDC4E", "\uD83D\uDCAA", "\uD83D\uDE4F", "\uD83D\uDC4F"
        };

        for (String emoji : emojis) {
            JButton emojiBtn = new JButton(emoji);
            emojiBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
            emojiBtn.addActionListener(e -> {
                messageField.setText(messageField.getText() + emoji);
                emojiDialog.dispose();
            });
            emojiDialog.getContentPane().add(emojiBtn);
        }

        emojiDialog.setVisible(true);
    }

    private void toggleRecording() {
        if (!isRecording) {
            startRecording();
            recordButton.setText("Dừng ghi âm");
            recordButton.setBackground(new Color(220, 53, 69));
            isRecording = true;
        } else {
            stopRecording();
            recordButton.setText("Ghi âm");
            recordButton.setBackground(new Color(0, 123, 255));
            isRecording = false;
            sendAudioFile();
        }
    }

    private void startRecording() {
        try {
            audioFormat = new AudioFormat(44100.0f, 16, 2, true, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
            targetDataLine.open(audioFormat);
            targetDataLine.start();

            audioFile = new File(TEMP_DIR + "recording_" + System.currentTimeMillis() + ".wav");
            new Thread(() -> {
                try {
                    AudioInputStream ais = new AudioInputStream(targetDataLine);
                    AudioSystem.write(ais, AudioFileFormat.Type.WAVE, audioFile);
                } catch (IOException e) {
                    appendToChat("Hệ thống", "Lỗi khi ghi âm: " + e.getMessage(), false);
                }
            }).start();
            appendToChat("Hệ thống", "Bắt đầu ghi âm...", false);
        } catch (LineUnavailableException e) {
            appendToChat("Hệ thống", "Lỗi khi khởi động ghi âm: " + e.getMessage(), false);
        }
    }

    private void stopRecording() {
        if (targetDataLine != null) {
            targetDataLine.stop();
            targetDataLine.close();
            appendToChat("Hệ thống", "Đã dừng ghi âm.", false);
        }
    }

    private void sendAudioFile() {
        if (audioFile == null || !audioFile.exists()) {
            appendToChat("Hệ thống", "Không có tệp ghi âm để gửi.", false);
            return;
        }

        String password = JOptionPane.showInputDialog(this, "Nhập mật khẩu để mã hóa tệp ghi âm:");
        if (password == null || password.trim().isEmpty()) {
            appendToChat("Hệ thống", "Mật khẩu không hợp lệ, hủy gửi tệp ghi âm.", false);
            return;
        }

        try {
            File encryptedFile = new File(TEMP_DIR + "temp_encrypted_" + audioFile.getName());
            FileEncryption.encryptFile(password, audioFile, encryptedFile);

            out.writeUTF("AUDIO");
            out.writeUTF(audioFile.getName());
            out.writeLong(encryptedFile.length());

            FileInputStream fis = new FileInputStream(encryptedFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
            fis.close();

            appendToChat(this.tenNhanVien, "Đã gửi tệp ghi âm: " + audioFile.getName() + " (Mã hóa với mật khẩu)", true);
            encryptedFile.delete();
            audioFile.delete();
        } catch (Exception e) {
            appendToChat("Hệ thống", "Lỗi khi gửi tệp ghi âm: " + e.getMessage(), false);
        }
    }

    private void receiveFile(String senderName, String fileName, long fileSize, boolean isHistory) throws IOException {
        File tempFile = new File(TEMP_DIR + "temp_" + System.currentTimeMillis() + "_" + fileName);
        FileOutputStream fos = new FileOutputStream(tempFile);
        byte[] buffer = new byte[4096];
        long bytesRead = 0;
        int count;
        while (bytesRead < fileSize && (count = in.read(buffer, 0, (int) Math.min(buffer.length, fileSize - bytesRead))) != -1) {
            fos.write(buffer, 0, count);
            bytesRead += count;
        }
        fos.close();

        if (bytesRead != fileSize) {
            appendToChat("Hệ thống", "Lỗi: Không nhận đủ dữ liệu tệp " + fileName, false);
            tempFile.delete();
            return;
        }

        if (!isHistory) {
            int option = JOptionPane.showConfirmDialog(this,
                    "Bạn có muốn tải tệp: " + fileName + " (" + fileSize + " bytes)?",
                    "Tệp nhận được", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                processFileDownload(senderName, fileName, tempFile);
                tempFile.delete();
            } else {
                pendingFiles.put(fileName, tempFile);
                reloadButton.setEnabled(true);
            }
        } else {
            pendingFiles.put(fileName, tempFile);
            reloadButton.setEnabled(true);
        }
    }

    private void receiveImage(String senderName, String imageName, long imageSize, boolean isHistory) throws IOException {
        File imageFile = new File(TEMP_DIR + imageName);
        FileOutputStream fos = new FileOutputStream(imageFile);
        byte[] buffer = new byte[4096];
        long bytesRead = 0;
        int count;
        while (bytesRead < imageSize && (count = in.read(buffer, 0, (int) Math.min(buffer.length, imageSize - bytesRead))) != -1) {
            fos.write(buffer, 0, count);
            bytesRead += count;
        }
        fos.close();

        showImageInChat(imageFile);

        try {
            String key = JOptionPane.showInputDialog(this, "Nhập mã để giải mã thông điệp trong ảnh:");
            if (key != null && !key.trim().isEmpty()) {
                String secret = ImageSteganography.extractMessage(imageFile.getAbsolutePath(), key.trim());
                if (secret != null && !secret.isEmpty()) {
                    appendToChat(senderName, "Tin nhắn giấu trong ảnh: " + secret, false);
                } else {
                    appendToChat(senderName, "Không tìm thấy hoặc mã sai.", false);
                }
            } else {
                appendToChat(senderName, "Không nhập mã. Bỏ qua giải mã.", false);
            }
        } catch (Exception e) {
            appendToChat(senderName, "Lỗi giải mã: " + e.getMessage(), false);
        } finally {
            if (!isHistory) {
                imageFile.delete();
            } else {
                pendingFiles.put(imageName, imageFile);
                reloadButton.setEnabled(true);
            }
        }
    }

    private void receiveAudio(String senderName, String audioName, long audioSize, boolean isHistory) throws IOException {
        File tempFile = new File(TEMP_DIR + "temp_" + System.currentTimeMillis() + "_" + audioName);
        FileOutputStream fos = new FileOutputStream(tempFile);
        byte[] buffer = new byte[4096];
        long bytesRead = 0;
        int count;
        while (bytesRead < audioSize && (count = in.read(buffer, 0, (int) Math.min(buffer.length, audioSize - bytesRead))) != -1) {
            fos.write(buffer, 0, count);
            bytesRead += count;
        }
        fos.close();

        String password = JOptionPane.showInputDialog(this, "Nhập mật khẩu để giải mã tệp ghi âm: " + audioName);
        if (password == null || password.trim().isEmpty()) {
            appendToChat(senderName, "Mật khẩu không hợp lệ, không thể giải mã tệp ghi âm: " + audioName, false);
            pendingFiles.put(audioName, tempFile);
            reloadButton.setEnabled(true);
            return;
        }

        File decryptedFile = null;
        try {
            decryptedFile = new File(TEMP_DIR + "decrypted_" + audioName);
            FileEncryption.decryptFile(password, tempFile, decryptedFile);

            appendAudioToChat(audioName, decryptedFile);
            pendingFiles.put(audioName, decryptedFile);
            reloadButton.setEnabled(true);
        } catch (javax.crypto.BadPaddingException | javax.crypto.IllegalBlockSizeException e) {
            appendToChat(senderName, "Mật khẩu sai, không thể giải mã tệp ghi âm: " + audioName, false);
            pendingFiles.put(audioName, tempFile);
            reloadButton.setEnabled(true);
        } catch (Exception e) {
            appendToChat(senderName, "Lỗi khi giải mã tệp ghi âm: " + e.getMessage(), false);
            pendingFiles.put(audioName, tempFile);
            reloadButton.setEnabled(true);
        } finally {
            tempFile.delete();
        }
    }

    private void processFileDownload(String senderName, String fileName, File encryptedFile) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(fileName));
        if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            appendToChat(senderName, "Hủy lưu tệp: " + fileName, false);
            return;
        }

        File saveFile = fileChooser.getSelectedFile();
        try {
            FileOutputStream fos = new FileOutputStream(saveFile);
            FileInputStream fis = new FileInputStream(encryptedFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            fis.close();
            fos.close();

            String password = JOptionPane.showInputDialog(this, "Nhập mật khẩu để giải mã tệp:");
            if (password == null || password.trim().isEmpty()) {
                appendToChat(senderName, "Mật khẩu không hợp lệ, không thể giải mã tệp: " + fileName, false);
                pendingFiles.put(fileName, saveFile);
                reloadButton.setEnabled(true);
                return;
            }

            File decryptedFile = new File(saveFile.getParent(), "decrypted_" + saveFile.getName());
            FileEncryption.decryptFile(password, saveFile, decryptedFile);
            appendToChat(senderName, fileName + " (đã lưu tại: " + decryptedFile.getAbsolutePath() + ")", false);
            pendingFiles.put(fileName, saveFile);
            reloadButton.setEnabled(true);
        } catch (javax.crypto.BadPaddingException | javax.crypto.IllegalBlockSizeException e) {
            appendToChat(senderName, "Mật khẩu sai, không thể giải mã tệp: " + fileName, false);
            pendingFiles.put(fileName, saveFile);
            reloadButton.setEnabled(true);
        } catch (IOException e) {
            appendToChat(senderName, "Lỗi I/O khi xử lý tệp: " + e.getMessage(), false);
            pendingFiles.put(fileName, saveFile);
            reloadButton.setEnabled(true);
        } catch (Exception e) {
            appendToChat(senderName, "Lỗi không xác định khi giải mã tệp: " + e.getMessage(), false);
            pendingFiles.put(fileName, saveFile);
            reloadButton.setEnabled(true);
        }
    }

    private void reloadFile() {
        if (pendingFiles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có tệp nào để tải lại!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] fileNames = pendingFiles.keySet().toArray(new String[0]);
        String selectedFile = (String) JOptionPane.showInputDialog(this, "Chọn tệp để tải lại:", "Tải lại tệp",
                JOptionPane.QUESTION_MESSAGE, null, fileNames, fileNames[0]);
        if (selectedFile == null) return;

        File tempFile = pendingFiles.get(selectedFile);
        processFileDownload(selectedFile, selectedFile, tempFile);
        tempFile.delete();
        if (pendingFiles.isEmpty()) {
            reloadButton.setEnabled(false);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new ChatUI_nhanvien(1); // Thay 1 bằng idTaiKhoan thực tế
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}