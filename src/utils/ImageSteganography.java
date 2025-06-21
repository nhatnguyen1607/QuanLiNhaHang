package utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Base64;

public class ImageSteganography {

    // Giấu thông điệp vào ảnh
    public static void hideMessage(String originalImagePath, String encodedImagePath, String secretMessage, String key) throws IOException {
        BufferedImage image = ImageIO.read(new File(originalImagePath));
        if (image == null) throw new IOException("Không thể đọc ảnh nguồn.");

        String encryptedMessage = xorEncrypt("@@START" + secretMessage, key);
        String binaryMessage = toBinary(encryptedMessage) + "00000000"; // Kết thúc bằng 8 bit 0

        int messageIndex = 0;
        outer:
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (messageIndex >= binaryMessage.length()) break outer;

                int pixel = image.getRGB(x, y);
                int blue = pixel & 0xFF;

                int bit = binaryMessage.charAt(messageIndex) - '0';
                blue = (blue & 0xFE) | bit;

                int newPixel = (pixel & 0xFFFFFF00) | blue;
                image.setRGB(x, y, newPixel);
                messageIndex++;
            }
        }

        ImageIO.write(image, "png", new File(encodedImagePath));
    }

    // Trích xuất thông điệp từ ảnh
    public static String extractMessage(String encodedImagePath, String key) throws IOException {
        BufferedImage image = ImageIO.read(new File(encodedImagePath));
        if (image == null) throw new IOException("Không thể đọc ảnh để giải mã.");

        StringBuilder binaryMessage = new StringBuilder();
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = image.getRGB(x, y);
                int blue = pixel & 0xFF;
                binaryMessage.append(blue & 1);
            }
        }

        String binaryStr = binaryMessage.toString();
        int endIndex = binaryStr.indexOf("00000000");
        if (endIndex == -1) return null;

        String encryptedBase64 = fromBinary(binaryStr.substring(0, endIndex));
        String decrypted = xorDecrypt(encryptedBase64, key);

        return decrypted.startsWith("@@START") ? decrypted.substring(7) : null;
    }

    // Mã hóa XOR + Base64
    private static String xorEncrypt(String text, String key) {
        byte[] textBytes = text.getBytes();
        byte[] keyBytes = key.getBytes();
        byte[] result = new byte[textBytes.length];

        for (int i = 0; i < textBytes.length; i++) {
            result[i] = (byte) (textBytes[i] ^ keyBytes[i % keyBytes.length]);
        }

        return Base64.getEncoder().encodeToString(result);
    }

    // Giải mã XOR + Base64
    private static String xorDecrypt(String encodedBase64, String key) {
        byte[] encryptedBytes = Base64.getDecoder().decode(encodedBase64);
        byte[] keyBytes = key.getBytes();
        byte[] result = new byte[encryptedBytes.length];

        for (int i = 0; i < encryptedBytes.length; i++) {
            result[i] = (byte) (encryptedBytes[i] ^ keyBytes[i % keyBytes.length]);
        }

        return new String(result);
    }

    // Chuyển chuỗi sang nhị phân
    private static String toBinary(String message) {
        StringBuilder binary = new StringBuilder();
        for (char c : message.toCharArray()) {
            binary.append(String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0'));
        }
        return binary.toString();
    }

    // Chuyển nhị phân về chuỗi
    private static String fromBinary(String binary) {
        StringBuilder message = new StringBuilder();
        for (int i = 0; i + 7 < binary.length(); i += 8) {
            String byteStr = binary.substring(i, i + 8);
            message.append((char) Integer.parseInt(byteStr, 2));
        }
        return message.toString();
    }
}
