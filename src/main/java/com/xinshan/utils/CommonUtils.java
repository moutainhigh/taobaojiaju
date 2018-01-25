package com.xinshan.utils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by jonson.xu on 15-4-23.
 */
public class CommonUtils {
    private static boolean isLoad = false;
    public static String domain;
    public static String CHARSET;
    public static boolean TEST;

    public static String WEBSOCKETKEY;
    public static String REDIS_HOST;
    public static String REDIS_PORT;
    public static String REDIS_PASSWORD;
    public static String WEB_INF_PATH;
    public static String CLASSES_PATH;

    public static String FILE_DOWNLOAD_TMP;
    public static String META_INF_PATH;

    public static String WX_SERVICE_APPID;
    public static String WX_SERVICE_SECRET;

    public static String FILE_UPLOAD_DIR;//文件上传保存路径，保存上传的导入文件
    public static String QRCODE_DIR;//商品二维码地址

    public static String WEBSOCKET_URL;
    public static Boolean WEBSOCKET_SERVER = false;
    public static Boolean WEBSOCKET_CLIENT = false;

    public static String phone_number_pattern = "";

    public static void init(HttpServletRequest request) {
        if (!isLoad) {
            WEB_INF_PATH = request.getSession().getServletContext().getRealPath("WEB-INF");
            META_INF_PATH = request.getSession().getServletContext().getRealPath("META-INF");
            CLASSES_PATH = WEB_INF_PATH + "/classes";
            try {
                load(CLASSES_PATH);
                isLoad = true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void init(String url) {
        if (!isLoad) {
            try {
                load(url);
                isLoad = true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void load(String path) throws IOException {
        Properties properties = new Properties();
        FileInputStream fileInputStream = new FileInputStream(new File(path + "/redis.properties"));
        properties.load(fileInputStream);
        REDIS_HOST = properties.getProperty("host");
        REDIS_PORT = properties.getProperty("port");
        REDIS_PASSWORD = properties.getProperty("password");

        fileInputStream = new FileInputStream(new File(CLASSES_PATH + "/websocket.properties"));
        properties.load(fileInputStream);
        WEBSOCKETKEY = properties.getProperty("websocketkey");

        fileInputStream = new FileInputStream(new File(CLASSES_PATH + "/config.properties"));
        properties.load(fileInputStream);
        FILE_DOWNLOAD_TMP = properties.getProperty("file_download_tmp");
        domain = properties.getProperty("domain");
        FILE_UPLOAD_DIR = properties.getProperty("file_upload_dir");
        QRCODE_DIR = properties.getProperty("qrcode_dir");
        CHARSET = properties.getProperty("charset");
        TEST = Boolean.parseBoolean(properties.getProperty("test"));
        WEBSOCKET_URL = properties.getProperty("websocket_url");
        WEBSOCKET_SERVER = Boolean.parseBoolean(properties.getProperty("websocket_server"));
        WEBSOCKET_CLIENT = Boolean.parseBoolean(properties.getProperty("websocket_client"));
        phone_number_pattern = properties.getProperty("phone_number_pattern");

        fileInputStream = new FileInputStream(new File(CLASSES_PATH + "/weixin.properties"));
        properties.load(fileInputStream);
        WX_SERVICE_APPID = properties.getProperty("service.appid");
        WX_SERVICE_SECRET = properties.getProperty("service.secret");
        fileInputStream.close();
    }
}
