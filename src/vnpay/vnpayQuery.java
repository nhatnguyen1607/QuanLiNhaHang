package vnpay;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 *
 * @author CTT VNPAY
 */
public class vnpayQuery {

	public static JsonObject queryTransaction(String vnp_TxnRef) throws IOException {
	    String vnp_RequestId = Config.getRandomNumber(8);
	    String vnp_Version = "2.1.0";
	    String vnp_Command = "querydr";
	    String vnp_TmnCode = Config.vnp_TmnCode;
	    String vnp_OrderInfo = "Kiem tra ket qua GD OrderId:" + vnp_TxnRef;
	    String vnp_TransDate = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7")).getTime());
	    String vnp_CreateDate = vnp_TransDate;
	    String vnp_IpAddr = "127.0.0.1";

	    JsonObject vnp_Params = new JsonObject();
	    vnp_Params.addProperty("vnp_RequestId", vnp_RequestId);
	    vnp_Params.addProperty("vnp_Version", vnp_Version);
	    vnp_Params.addProperty("vnp_Command", vnp_Command);
	    vnp_Params.addProperty("vnp_TmnCode", vnp_TmnCode);
	    vnp_Params.addProperty("vnp_TxnRef", vnp_TxnRef);
	    vnp_Params.addProperty("vnp_OrderInfo", vnp_OrderInfo);
	    vnp_Params.addProperty("vnp_TransactionDate", vnp_TransDate);
	    vnp_Params.addProperty("vnp_CreateDate", vnp_CreateDate);
	    vnp_Params.addProperty("vnp_IpAddr", vnp_IpAddr);

	    String hashData = String.join("|", vnp_RequestId, vnp_Version, vnp_Command, vnp_TmnCode, vnp_TxnRef, vnp_TransDate, vnp_CreateDate, vnp_IpAddr, vnp_OrderInfo);
	    String vnp_SecureHash = Config.hmacSHA512(Config.secretKey, hashData);
	    vnp_Params.addProperty("vnp_SecureHash", vnp_SecureHash);

	    URL url = new URL(Config.vnp_ApiUrl);
	    HttpURLConnection con = (HttpURLConnection) url.openConnection();
	    con.setRequestMethod("POST");
	    con.setRequestProperty("Content-Type", "application/json");
	    con.setDoOutput(true);
	    try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
	        wr.writeBytes(vnp_Params.toString());
	        wr.flush();
	    }
	    int responseCode = con.getResponseCode();
	    System.out.println("Response Code: " + responseCode);

	    JsonObject result = new JsonObject();
	    if (responseCode == HttpURLConnection.HTTP_OK) {
	        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
	            StringBuilder response = new StringBuilder();
	            String line;
	            while ((line = in.readLine()) != null) {
	                response.append(line);
	            }
	            result = new Gson().fromJson(response.toString(), JsonObject.class);
	        }
	    }
	    con.disconnect();
	    return result;
	}
}