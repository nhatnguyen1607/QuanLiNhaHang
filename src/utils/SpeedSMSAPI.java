package utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class SpeedSMSAPI {
    public static final String API_URL = "https://api.speedsms.vn/index.php";
    protected String mAccessToken;

    public SpeedSMSAPI(String accessToken) {
        this.mAccessToken = accessToken;
        System.out.println("Initialized with Access Token: " + mAccessToken);
    }

    public String getUserInfo() throws IOException {
        URL url = new URL(API_URL + "/user/info");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        String userCredentials = mAccessToken + ":x";
        String basicAuth = "Basic " + Base64.getEncoder().encodeToString(userCredentials.getBytes());
        System.out.println("Authorization Header: " + basicAuth);
        conn.setRequestProperty("Authorization", basicAuth);

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine = "";
        StringBuffer buffer = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            buffer.append(inputLine);
        }
        in.close();
        return buffer.toString();
    }

    public String sendSMS(String to, String content, int type, String sender) throws IOException {
        String json = "{\"to\": [\"" + to + "\"], \"content\": \"" + EncodeNonAsciiCharacters(content) + "\", \"type\":" + type + ", \"brandname\":\"" + (sender != null && !sender.isEmpty() ? sender : "") + "\"}";
        URL url = new URL(API_URL + "/sms/send");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        String userCredentials = mAccessToken + ":x";
        String basicAuth = "Basic " + Base64.getEncoder().encodeToString(userCredentials.getBytes());
        System.out.println("Authorization Header (send): " + basicAuth);
        conn.setRequestProperty("Authorization", basicAuth);
        conn.setRequestProperty("Content-Type", "application/json");

        conn.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(json);
        wr.flush();
        wr.close();

        int responseCode = conn.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine = "";
        StringBuffer buffer = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            buffer.append(inputLine);
        }
        in.close();
        return buffer.toString();
    }

    private String EncodeNonAsciiCharacters(String value) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            int unit = (int) c;
            if (unit > 127) {
                String hex = String.format("%04x", (int) unit);
                String encodedValue = "\\u" + hex;
                sb.append(encodedValue);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}