package com.xinshan.utils.weixin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by mxt on 17-8-8.
 */
public class WeiXinServiceJSticket {
    private static String jsapi_ticket = null;
    private static Long expires_in = null;
    private static Long ticketTime = null;
    public static void initTicket() {
        if (jsapi_ticket == null || expires_in == null || ticketTime == null || System.currentTimeMillis() - ticketTime >= expires_in ) {
            String urlStr = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + WeiXinServiceUtils.getToken() + "&type=jsapi";
            try {
                URL url = new URL(urlStr);
                HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();

                conn.setDoInput(true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String s = null;
                while ((s = reader.readLine()) != null) {
                    sb.append(s);
                }
                reader.close();

                JSONObject jsonObject = JSON.parseObject(sb.toString());
                String errcode = jsonObject.get("errcode").toString();
                if (errcode.equals("0")) {
                    jsapi_ticket = jsonObject.get("ticket").toString();
                    expires_in = jsonObject.getLong("expires_in");
                    ticketTime = System.currentTimeMillis();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String getTicket() {
        initTicket();
        return jsapi_ticket;
    }
}
