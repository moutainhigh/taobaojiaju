package com.xinshan.utils.shortMessage;

import com.alibaba.fastjson.JSONObject;
import com.xinshan.model.UserCheckCode;
import com.xinshan.utils.HttpUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

/**
 * Created by mxt on 16-12-27.
 */
public class ShortMessageUtils {
    public static String login = "fenghuang";
    public static String password = "d01e9306";
    public static String sid = "1152";
    public static String tid = "1090";
    public static String url = "http://sms.api.ueidc.com";

    /**
     *
     * @param mobile
     * @param checkCode
     * @throws
     */
    public static void sendCheckCode(String mobile, String checkCode, UserCheckCode userCheckCode) throws IOException {
        StringBuffer param = new StringBuffer();
        param.append("category=").append("sms");
        param.append("&action=").append("send");
        param.append("&mobile=").append(mobile);
        param.append("&content=").append("您的验证码是：").append(checkCode);
        param.append("&login=").append(ShortMessageUtils.login);
        param.append("&password=").append(ShortMessageUtils.password);

        userCheckCode.setMsg_param(param.toString());
        String s = HttpUtils.sendPost(ShortMessageUtils.url, param.toString());
        userCheckCode.setMsg_result(s);
    }
    /*<?xml version="1.0" encoding="utf-8"?>
    <property>
    <returncode>200</returncode>
    <message>发送成功</message>
    <time>1482893737</time>
    <timestamp>1482893737</timestamp>
    </property>*/
    private static int result(String s) {
        int resultCode = 0;
        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(new ByteArrayInputStream(s.getBytes()));
            Element element = document.getRootElement();
            Iterator<Element> iterator = element.elementIterator();
            while (iterator.hasNext()) {
                Element element1 = iterator.next();
                String returncode = (String) element1.getData();
                String name = element1.getName();
                if (name.equals("returncode")) {
                    resultCode = Integer.parseInt(returncode);
                }
            }
        }catch (DocumentException e) {
            e.printStackTrace();
        }
        return resultCode;
    }

    public static void main(String[] args) {
        String s = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<property>\n" +
                "    <returncode>200</returncode>\n" +
                "    <message>发送成功</message>\n" +
                "    <time>1482893737</time>\n" +
                "    <timestamp>1482893737</timestamp>\n" +
                "</property>";
        result(s);
    }

}
