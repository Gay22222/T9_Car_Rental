//// File: CreateOrder.java
//package com.uit.carrental.Model;
//
//import android.os.Build;
//import androidx.annotation.RequiresApi;
//
//import com.uit.carrental.Service.ZaloPay.Constant.AppInfo;
//import com.uit.carrental.Service.ZaloPay.Helper.HMac.HMacUtil;
//
//import org.json.JSONObject;
//
//import java.io.OutputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.nio.charset.StandardCharsets;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.TimeZone;
//
//public class CreateOrder {
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public JSONObject createOrder(String appTransId) {
//        try {
//            long amount = 1000; // có thể lấy từ amount ngoài truyền vào
//
//            // Lấy thời gian theo chuẩn GMT+7
//            SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
//            sdf.setTimeZone(TimeZone.getTimeZone("GMT+7"));
//            String appTime = sdf.format(new Date());
//
//            JSONObject order = new JSONObject();
//            order.put("app_id", AppInfo.APP_ID);
//            order.put("app_user", "user123"); // user định danh (tuỳ chỉnh)
//            order.put("app_time", System.currentTimeMillis()); // thời gian tạo đơn
//            order.put("amount", amount);
//            order.put("app_trans_id", appTime + "_" + appTransId); // định dạng yêu cầu
//            order.put("embed_data", "{}");
//            order.put("item", "[]");
//            order.put("description", "Thanh toán đơn thuê xe CarRental: " + appTransId);
//            order.put("bank_code", "zalopayapp"); // App mobile payment
//
//            // Build data to hash
//            String data = AppInfo.APP_ID + "|" + appTime + "_" + appTransId + "|" + AppInfo.APP_USER + "|" + amount + "|" + AppInfo.APP_NAME + "|{}|[]";
//            String mac = HMacUtil.HMacHexStringEncode("HmacSHA256", AppInfo.MAC_KEY, data);
//            order.put("mac", mac);
//
//            // Gửi HTTP request
//            URL url = new URL(AppInfo.URL_CREATE_ORDER);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("POST");
//            connection.setRequestProperty("Content-Type", "application/json");
//            connection.setDoOutput(true);
//
//            OutputStream os = connection.getOutputStream();
//            os.write(order.toString().getBytes(StandardCharsets.UTF_8));
//            os.close();
//
//            int responseCode = connection.getResponseCode();
//            if (responseCode == HttpURLConnection.HTTP_OK) {
//                java.util.Scanner s = new java.util.Scanner(connection.getInputStream()).useDelimiter("\\A");
//                String response = s.hasNext() ? s.next() : "";
//                s.close();
//                return new JSONObject(response);
//            } else {
//                throw new RuntimeException("Failed : HTTP error code : " + responseCode);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//}
