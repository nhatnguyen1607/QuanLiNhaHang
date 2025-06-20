package utils;

import javax.mail.*;
import javax.mail.internet.*;

import java.time.LocalDateTime;
import java.util.Properties;

public class SMSService2 {
    public static void sendSMS(String phoneNumber, String message) throws MessagingException {
        String host = "smtp.gmail.com";
        String username = "nhatnguyen4369@gmail.com";
        String password = "znkyvytrgagyyczx"; 
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(username));
            String emailDomain = "vn"; // Mobifone
            if (phoneNumber.startsWith("09") || phoneNumber.startsWith("07")) {
                emailDomain = "vn"; 
            } else if (phoneNumber.startsWith("08") || phoneNumber.startsWith("05")) {
                emailDomain = "viettel.vn"; // Viettel
            } else if (phoneNumber.startsWith("09") || phoneNumber.startsWith("08")) {
                emailDomain = "vip.gpc.vn"; // Vinaphone
            }
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(phoneNumber + "@" + emailDomain));           
            mimeMessage.setSubject("SMS");
            mimeMessage.setText(message);

            Transport.send(mimeMessage);
            System.out.println("SMS sent to " + phoneNumber);
            String emailAddress = phoneNumber +"@"+ emailDomain;
            String timestamp = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            System.out.println("SMS sent to " + emailAddress + " at " + timestamp);
        } catch (MessagingException e) {
            System.out.println("Failed to send SMS: " + e.getMessage());
            throw e;
        }
    }
}