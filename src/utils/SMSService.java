//package utils;
//
//import com.twilio.Twilio;
//import com.twilio.rest.api.v2010.account.Message;
//import com.twilio.type.PhoneNumber;
//
//public class SMSService {
//    // Thay YOUR_ACCOUNT_SID và YOUR_AUTH_TOKEN bằng thông tin từ Twilio
//    public static final String ACCOUNT_SID = "USc7da47733e366feb61706c96683ccbc0";
//    public static final String AUTH_TOKEN = "YOUR_AUTH_TOKEN";
//    public static final String TWILIO_PHONE_NUMBER = "+1 936 937 2144"; // Số Twilio cấp
//
//    static {
//        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
//    }
//
//    public static void sendSMS(String to, String message) {
//        try {
//            Message.creator(
//                new PhoneNumber(to), // Số nhận (ví dụ: "+84912345678")
//                new PhoneNumber(TWILIO_PHONE_NUMBER), // Số gửi từ Twilio
//                message
//            ).create();
//            System.out.println("Gửi SMS thành công đến " + to + ": " + message);
//        } catch (Exception e) {
//            System.err.println("Lỗi khi gửi SMS: " + e.getMessage());
//            throw new RuntimeException("Gửi SMS thất bại: " + e.getMessage());
//        }
//    }
//}