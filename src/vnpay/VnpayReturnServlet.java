package vnpay;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class VnpayReturnServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Lấy các tham số từ VNPAY
        String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
        String vnp_TxnRef = request.getParameter("vnp_TxnRef");
        String vnp_Amount = request.getParameter("vnp_Amount");
        String vnp_TransactionNo = request.getParameter("vnp_TransactionNo");
        String vnp_BankCode = request.getParameter("vnp_BankCode");
        String vnp_PayDate = request.getParameter("vnp_PayDate");

        // Xác thực SecureHash (tùy chọn, cần thêm logic từ Config.hmacSHA512)
        // Ví dụ đơn giản, bạn cần tích hợp logic xác thực hash từ VNPAY
        boolean isValid = true; // Thay bằng logic xác thực thực tế

        // Chuẩn bị tham số để chuyển đến trang HTML
        String redirectUrl = "vnpay_return.html?vnp_ResponseCode=" + vnp_ResponseCode
                + "&vnp_TxnRef=" + vnp_TxnRef
                + "&vnp_Amount=" + vnp_Amount
                + "&vnp_TransactionNo=" + vnp_TransactionNo
                + "&vnp_BankCode=" + vnp_BankCode
                + "&vnp_PayDate=" + vnp_PayDate
                + "&isValid=" + isValid;

        // Chuyển hướng đến trang HTML
        response.sendRedirect(redirectUrl);
    }
}