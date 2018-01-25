package com.xinshan.utils;

import com.alibaba.fastjson.JSONObject;

import java.text.SimpleDateFormat;

/**
 * Created by mxt on 15-8-6.
 */
public class TokenUtils {

    public static boolean setToken(String token, JSONObject employee) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("time", dateFormat.format(DateUtil.currentDate()));
            jsonObject.put("linked", employee);
            RedisUtils.hset(CommonUtils.WEBSOCKETKEY, token, jsonObject.toString());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Object getToken(String token) {
        return RedisUtils.hget(CommonUtils.WEBSOCKETKEY, token);
    }
}
