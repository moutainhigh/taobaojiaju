package com.xinshan.utils.qrcode;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * 二维码生成和解析
 * Created by mxt on 16-10-31.
 */
public class QRCodeUtil {
    public static void main(String[] args) {
        long a = System.currentTimeMillis();
        createQRCode("/home/mxt/", "sign_live_1", "http://activity.xsdangjian.com/live/admin/lottery_sign/sign/1");
        createQRCode("/home/mxt/", "sign_live_2", "http://activity.xsdangjian.com/live/admin/lottery_sign/sign/2");
        createQRCode("/home/mxt/", "sign_live_3", "http://activity.xsdangjian.com/live/admin/lottery_sign/sign/3");
        createQRCode("/home/mxt/", "sign_live_4", "http://activity.xsdangjian.com/live/admin/lottery_sign/sign/4");
        //String decode = decode(new File("/home/mxt/sign1.png"));
        //System.out.println(decode);
        long b = System.currentTimeMillis();
        System.out.println(b-a);
    }
    /**
     * 生成二维码
     * @param filename
     * @param content
     * @param width
     * @param height
     * @param tmpFilePath
     * @return 二维码图片文件
     */
    public static File createQRCode(String tmpFilePath, String filename, String content, int width, int height) {
        try {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            Map hints = new HashMap();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            content = new String(content.getBytes(Charset.forName("utf-8")), "iso-8859-1");
            BitMatrix bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, width, height);
            File file = new File(tmpFilePath+"/"+filename+".png");
            MatrixToImageWriter.writeToFile(bitMatrix, "png", file);
            return file;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成二维码
     * @param tmpFilePath
     * @param filename
     * @param content
     * @return
     */
    public static File createQRCode(String tmpFilePath, String filename, String content) {
        int defaultWidth = 600;
        int defaultHeight = 600;
        return createQRCode(tmpFilePath, filename, content, defaultWidth, defaultHeight);
    }

    /**
     * 生成带有logo的二维码
     * @param tmpFilePath
     * @param filename
     * @param content
     * @param width
     * @param height
     * @param logo  logo图片
     * @return
     */
    public static File createQRCodeWithLogo(String tmpFilePath, String filename, String content, int width, int height, File logo) {
        File file = createQRCode(tmpFilePath, filename, content, width, height);
        if (file == null) {
            return null;
        }
        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            Graphics2D graphics2D = bufferedImage.createGraphics();
            BufferedImage image = ImageIO.read(logo);
            // 计算图片放置位置
            int x = (width - image.getWidth()) / 2;
            int y = (height - image.getHeight()) / 2;

            graphics2D.drawImage(image, x ,y ,image.getWidth(), image.getHeight(), null);
            graphics2D.dispose();
            ImageIO.write(bufferedImage, "png", file);
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 识别二维码
     * @param file
     * @return
     */
    public static String decode(File file) {
        try {
            BufferedImage image = ImageIO.read(file);
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Map hints = new HashMap();
            hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
            Result result = new MultiFormatReader().decode(bitmap, hints);
            return result.getText();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
