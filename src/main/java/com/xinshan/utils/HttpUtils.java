package com.xinshan.utils;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by mxt on 17-5-8.
 */
public class HttpUtils {

    public static String httpGet(String urlStr) throws IOException {
        System.out.println("http:"+urlStr);
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestMethod("POST");
        conn.setUseCaches(false);
        conn.setReadTimeout(3000);
        conn.setConnectTimeout(3000);

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
        String s = null;
        StringBuilder sb = new StringBuilder();
        while ((s = reader.readLine())!=null) {
            sb.append(s);
        }
        reader.close();
        System.out.println("result:"+sb.toString());
        return sb.toString();
    }

    public static String sendPost(String urlStr, String parameters) throws IOException {
        System.out.println("https:"+urlStr + "\t" + parameters);
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestMethod("POST");
        conn.setUseCaches(false);
        conn.setReadTimeout(3000);
        conn.setConnectTimeout(3000);
        OutputStream output = conn.getOutputStream();
        output.write(parameters.getBytes("utf-8"));
        output.flush();

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
        String s = null;
        StringBuilder sb = new StringBuilder();
        while ((s = reader.readLine())!=null) {
            sb.append(s);
        }
        reader.close();
        System.out.println("result:"+sb.toString());
        return sb.toString();
    }

    public static String https(String urlString) {
        try {
            System.out.println("https:"+urlString);
            URL url = new URL(urlString);
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
            InputStreamReader inputStreamReader = new InputStreamReader(httpsURLConnection.getInputStream());
            int responseInt = inputStreamReader.read();
            StringBuffer stringBuffer = new StringBuffer();
            while (responseInt != -1) {
                stringBuffer.append((char) responseInt);
                responseInt = inputStreamReader.read();
            }
            String tokenString = stringBuffer.toString();
            System.out.println("result:"+tokenString);
            return tokenString;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String httpsPost(String urlString, String postData) {
        try {
            System.out.println("https:"+urlString + "\t" + postData);
            URL url = new URL(urlString);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setReadTimeout(3000);
            conn.setConnectTimeout(3000);
            OutputStream output = conn.getOutputStream();
            output.write(postData.getBytes("utf-8"));
            output.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
            String s = null;
            StringBuilder sb = new StringBuilder();
            while ((s = reader.readLine())!=null) {
                sb.append(s);
            }
            reader.close();
            System.out.println("result:"+sb.toString());
            return sb.toString();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
