package utils;

import okhttp3.*;
import java.io.IOException;

public class SMSService {
    private static final String API_URL = "https://rest.esms.vn/MainService.svc/json/SendMultipleMessage_V4_post_json/";
    private static final String API_KEY = "79F613C55736150786D84EDFBD5D2D";
    private static final String SECRET_KEY = "8C7F6544B7BBF067B4D3503A277A8F";
    private static final String BRAND_NAME = "2MN"; 
    private static final String SMS_TYPE = "2";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static void sendSMS(String phoneNumber, String message) throws IOException {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống");
        }

        phoneNumber = normalizePhoneNumber(phoneNumber);

        OkHttpClient client = new OkHttpClient();
        String json = "{" +
                "\"ApiKey\": \"" + API_KEY + "\"," +
                "\"SecretKey\": \"" + SECRET_KEY + "\"," +
                "\"IsUnicode\": 0," +
                "\"Brandname\": \"" + BRAND_NAME + "\"," +
                "\"SmsType\": " + SMS_TYPE + "," +
                "\"Phone\": \"" + phoneNumber + "\"," +
                "\"Content\": \"" + message + "\"" +
                "}";

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println("Response code: " + response.code());
            if (response.body() == null) {
                throw new IOException("Không nhận được response từ eSMS");
            }
            String responseBody = response.body().string();
            System.out.println("Full response from eSMS: " + responseBody);
            if (!response.isSuccessful() || !responseBody.contains("\"CodeResult\":100")) {
                throw new IOException("Gửi SMS thất bại: " + responseBody);
            }
        } catch (IOException e) {
            System.out.println("IOException details: " + e.getMessage());
            throw e;
        }
    }

    private static String normalizePhoneNumber(String phoneNumber) {
        phoneNumber = phoneNumber.replaceAll("[^0-9]", "");
        if (phoneNumber.startsWith("0")) {
            phoneNumber = "+84" + phoneNumber.substring(1);
        } else if (!phoneNumber.startsWith("+84")) {
            phoneNumber = "+84" + phoneNumber;
        }
        return phoneNumber;
    }
}