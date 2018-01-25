package com.xinshan.utils;

import com.alibaba.fastjson.JSONObject;
import com.xinshan.utils.qrcode.QRCodeUtil;
import com.xinshan.utils.shortMessage.ShortMessageUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mxt on 16-7-11.
 */
public class Test {

    public static void main(String[] args) throws IOException {
        //QRCodeUtil.createQRCode("/home/mxt/", "supermarket1", "http://mall.hnxinshan.com", 600, 600);
        //String s = "%5B%7B%22time%22%3A1512983604%2C%22kw%22%3A%22java%20%E6%96%B9%E6%B3%95%E8%B0%83%E7%94%A8%E9%98%9F%E5%88%97%22%7D%2C%7B%22time%22%3A1513136235%2C%22kw%22%3A%22wap%E5%9B%BE%E7%89%87%E6%A0%BC%E5%BC%8F%22%7D%2C%7B%22time%22%3A1513136269%2C%22kw%22%3A%22webp%22%7D%2C%7B%22time%22%3A1513156069%2C%22kw%22%3A%22%E6%89%8B%E6%9C%BA%20webp%22%7D%2C%7B%22time%22%3A1513416381%2C%22kw%22%3A%22java%20%E6%96%87%E4%BB%B6%E8%BF%BD%E5%8A%A0%E5%86%99%E5%85%A5%20%E6%AF%94%E8%BE%83%22%7D%2C%7B%22time%22%3A1513558083%2C%22kw%22%3A%22java%20sha-1%22%7D%2C%7B%22time%22%3A1513563004%2C%22kw%22%3A%22rabbitmq%20java%22%2C%22fq%22%3A2%7D%2C%7B%22time%22%3A1513906831%2C%22kw%22%3A%22spring%22%7D%2C%7B%22time%22%3A1513906854%2C%22kw%22%3A%22spring%20ioc%E5%8E%9F%E7%90%86%22%7D%5D";
        //System.out.println(URLDecoder.decode(s, "UTF-8"));
        //QRCodeUtil.createQRCode("/home/mxt/", "lotteryStatus", "http://activity.xsdangjian.com/live_dev/admin/lottery_sign/lotteryStatus", 400, 400);
        String s = "é¼\\u008Eæ\\u0099\\u009F";
        System.out.println(new String(s.getBytes("gb2312"), "utf-8"));
    }

    public static void testRotate() {
        BufferedImage buffImg = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = buffImg.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 200, 200);
        g.setColor(Color.black);
        Font font = new Font(null, 0 , 40);
        g.setFont(font);
        g.rotate(0.20, 100, 100);
        g.drawString("A", 100 , 100);
        g.rotate(0.2, 100, 100);
        g.drawString("A", 100 , 100);
        g.rotate(0.3, 100, 100);
        g.drawString("A", 100 , 100);
        g.rotate(0.4, 100, 100);
        g.drawString("A", 100 , 100);
        g.rotate(0.5, 100, 100);
        g.drawString("A", 100 , 100);
        g.rotate(0.6, 100, 100);
        g.drawString("A", 100 , 100);
        g.rotate(0.7, 100, 100);
        g.drawString("A", 100 , 100);


        try {
            FileOutputStream fileOutputStream = new FileOutputStream("/home/mxt/a.png");
            ImageIO.write(buffImg, "png", fileOutputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Test1 {
        public Test1(int id, String name) {
            this.name = name;
            this.id = id;
        }

        private String name;
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", name);
            jsonObject.put("id", id);
            return jsonObject.toString();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Test1) {
                Test1 test1 = (Test1) obj;
                return test1.getName().equals(getName());
            }
            return false;
        }
    }

    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }
    public static void sendCheckCode(String mobile, String checkCode) throws IOException {
        //category=sms&action=send&mobile=$mobile&content=$content&login=testsms&password=smspwd
        StringBuffer param = new StringBuffer();
        param.append("category=").append("sms");
        param.append("&action=").append("send");
        param.append("&mobile=").append(mobile);
        param.append("&content=").append("您的验证码是：123456");
        param.append("&login=").append(ShortMessageUtils.login);
        param.append("&password=").append(ShortMessageUtils.password);

        String s = HttpUtils.sendPost(ShortMessageUtils.url, param.toString());
        System.out.println(s);
    }

    private static void testMatcher(List<String> list, String s) {
        Pattern pattern = Pattern.compile(s);
        Matcher matcher = null;
        for (int i = 0; i < list.size(); i++) {
            matcher = pattern.matcher(list.get(i));
            if (matcher.find()) {
                System.out.println(list.get(i));
            }
        }
    }

    private static int change(int[] a, int times) {
        for (int i = 0; i < a.length - 1; i++) {
            int b = a[i];
            int c = a[i+1];
            int[] aa = new int[a.length - 1];
            if (i == 0) {
                aa[0] = b+c;
                int n = 1;
                for (int j = i+2; j < a.length ; j++) {
                    aa[n] = a[j];
                    n++;
                }
            }else if(i == a.length - 2){
                int n = 0;
                for (int j = 0; j < a.length - 2; j++) {
                    aa[n] = a[j];
                    n++;
                }
                aa[aa.length - 1] = b+c;
            }else {
                int n = 0;
                for (int j = 0; j < i; j++) {
                    aa[n] = a[j];
                    n++;
                }
                aa[n] = b+c;
                n++;
                for (int j = i+2; j < a.length ; j++) {
                    aa[n] = a[j];
                    n++;
                }
            }
            times++;
            if (isHunWen(aa)) {
                return times;
            }else {
                return change(aa, times);
            }
        }
        return 0;
    }

    private static boolean isHunWen(int[] a) {
        int len = a.length;
        for (int i = 0; i < len/2; i++) {
            if (a[i] != a[len-i-1]){
                return false;
            }
        }
        System.out.println(Arrays.toString(a));
        return true;
    }

    private static long primeSum(final int range) {
//        作者：周秀敏
//        链接：https://www.zhihu.com/question/29580448/answer/49073251
//        来源：知乎
//        著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
        int i, k;
        HashMap<Integer, Long> S = new HashMap<Integer, Long>();
        int r = (int) Math.sqrt(range);
        int p = range / r;
        int[] V = new int[r + p - 1];
        k = r + 1;
        for (i = 1; i < k; i++) {
            V[i - 1] = range / i;
        }
        int count = 1;
        for (i = r + p - 2; i >= r; i--) {
            V[i] = count++;
        }
        // ArrayUtils.printArray(V);
        for (i = 0; i < V.length; i++) {
            S.put(V[i], ((long) V[i] * (V[i] + 1) / 2 - 1));
        }
        // System.out.println(S);
        Long sp, p2;
        for (p = 2; p < r + 1; p++) {
            if (S.get(p) > S.get(p - 1)) {
                sp = S.get(p - 1);
                p2 = (long) (p * p);
                for (i = 0; i < V.length; i++) {
                    if (V[i] < p2) {
                        break;
                    }
                    S.put(V[i], S.get(V[i]) - p * (S.get(V[i] / p) - sp));
                }
            }
        }

        return S.get(range);
    }
}
