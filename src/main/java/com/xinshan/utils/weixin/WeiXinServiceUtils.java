package com.xinshan.utils.weixin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xinshan.utils.CommonUtils;
import com.xinshan.utils.HttpUtils;

import javax.net.ssl.HttpsURLConnection;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by mxt on 16-12-23.
 */
public class WeiXinServiceUtils {

    private static String token;
    private static Long tokenExpire;
    private static Long tokenTime;

    private static void initToken() {
        if (tokenTime == null || tokenExpire == null || System.currentTimeMillis()/1000 - tokenTime >= tokenExpire || token == null) {
            String uriString = "https://api.weixin.qq.com/cgi-bin/token";
//            https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET
            StringBuffer sb = new StringBuffer();
            sb.append(uriString);
            sb.append("?grant_type=client_credential");
            sb.append("&appid=").append(CommonUtils.WX_SERVICE_APPID);
            sb.append("&secret=").append(CommonUtils.WX_SERVICE_SECRET);

            try {
                URL url = new URL(sb.toString());
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                InputStreamReader inputStreamReader = new InputStreamReader(httpsURLConnection.getInputStream());
                int responseInt = inputStreamReader.read();
                StringBuffer stringBuffer = new StringBuffer();
                while (responseInt != -1) {
                    stringBuffer.append((char) responseInt);
                    responseInt = inputStreamReader.read();
                }
                String tokenString = stringBuffer.toString();
//                {"access_token":"ACCESS_TOKEN","expires_in":7200}
                JSONObject jsonObject = JSON.parseObject(tokenString);
                if (jsonObject.containsKey("access_token")) {
                    tokenTime = System.currentTimeMillis()/1000;
                    token = jsonObject.getString("access_token");
                    tokenExpire = jsonObject.getLong("expires_in");
                } else {
                    //TODO 验证错误System.out.println(jsonObject.get("errcode"));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static String getToken() {
        initToken();
        return token;
    }

    /**
     * 使用微信code获取openid
     * @param code
     * @return
     */
    public static String getOpenidByCode(String code) {
        //https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
        StringBuffer sb = new StringBuffer();
        sb.append("https://api.weixin.qq.com/sns/oauth2/access_token?");
        sb.append("appid=").append(CommonUtils.WX_SERVICE_APPID);
        sb.append("&secret=").append(CommonUtils.WX_SERVICE_SECRET);
        sb.append("&code=").append(code);
        sb.append("&grant_type=authorization_code");
        String s = HttpUtils.https(sb.toString());
        JSONObject jsonObject = JSONObject.parseObject(s);
        if (jsonObject.get("openid") != null) {
            return jsonObject.get("openid").toString();
        }
        return null;
    }

    /**
     *
     * @param openid
     * @return
     */
    public static UserInfo getUserInfo(String openid) {
        //https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN
        StringBuffer sb = new StringBuffer();
        sb.append("https://api.weixin.qq.com/cgi-bin/user/info?");
        sb.append("access_token=").append(getToken());
        sb.append("&openid=").append(openid);
        sb.append("&lang=zh_CN");
        System.out.println(sb);
        String s = HttpUtils.https(sb.toString());
        System.out.println(s);
        if (s != null) {
            return JSONObject.parseObject(s, UserInfo.class);
        }
        return null;
    }

    public static JSONObject userList(String openid) {
        //https://api.weixin.qq.com/cgi-bin/user/get?access_token=ACCESS_TOKEN&next_openid=NEXT_OPENID
        StringBuffer sb = new StringBuffer();
        sb.append("https://api.weixin.qq.com/cgi-bin/user/get?");
        sb.append("access_token=").append(getToken());
        if (openid != null && !openid.equals("")) {
            sb.append("&openid=").append(openid);
        }
        String s = HttpUtils.https(sb.toString());
        if (s != null) {
            return JSONObject.parseObject(s);
        }
        return null;
    }

    public static JSONObject tagList() {
        //https://api.weixin.qq.com/cgi-bin/tags/get?access_token=ACCESS_TOKEN
        StringBuffer sb = new StringBuffer();
        sb.append("https://api.weixin.qq.com/cgi-bin/tags/get?");
        sb.append("access_token=").append(getToken());
        String s = HttpUtils.https(sb.toString());
        if (s != null) {
            return JSONObject.parseObject(s);
        }
        return null;
    }

    public static JSONObject userTag(String openid, int tagid) {
        //https://api.weixin.qq.com/cgi-bin/tags/members/batchtagging?access_token=ACCESS_TOKEN
        StringBuffer sb = new StringBuffer();
        sb.append("https://api.weixin.qq.com/cgi-bin/tags/members/batchtagging?");
        sb.append("access_token=").append(getToken());

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(openid);
        jsonObject.put("openid_list", jsonArray);
        jsonObject.put("tagid", tagid);

        String s = HttpUtils.httpsPost(sb.toString(), jsonObject.toString());
        JSONObject result = JSON.parseObject(s);
        if (result.get("errcode").toString().equals("0")) {
            return result;
        }
        return null;
    }
}
