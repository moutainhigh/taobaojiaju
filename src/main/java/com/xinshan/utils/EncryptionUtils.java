package com.xinshan.utils;

import sun.misc.BASE64Encoder;

import java.security.MessageDigest;


/**
 * Created by jonson.xu on 15-3-25.
 */
public class EncryptionUtils {
    private final static String beginkey = "bceogdiinnnging";
    private final static String endkey = "ceondding";

    public static String get32BitMD5(String str) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            MessageDigest messageDigest = null;
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
            byte[] byteArray = messageDigest.digest();
            for (int i = 0; i < byteArray.length; i++) {
                if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                    stringBuffer.append("0").append(
                            Integer.toHexString(0xFF & byteArray[i]));
                else
                    stringBuffer.append(Integer.toHexString(0xFF & byteArray[i]));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stringBuffer.toString();
    }

    public static String encryption(String str) {
//        if (str == null || "".equals(str)) str = "111111";
        BASE64Encoder base64en = new BASE64Encoder();
        String pwd = base64en.encode(beginkey.getBytes());
        pwd = pwd + get32BitMD5(str);
        pwd = pwd + base64en.encode(endkey.getBytes());
        pwd = get32BitMD5(pwd);
        return pwd;
    }
}
