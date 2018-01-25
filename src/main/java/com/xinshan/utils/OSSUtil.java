package com.xinshan.utils;

import com.aliyun.oss.OSSClient;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * Created by mxt on 17-10-16.
 */
public class OSSUtil {

    public static String endpoint = "oss-cn-beijing.aliyuncs.com";
    public static String accessId = "LTAIpyavQLSijwrr";
    public static String accessKey = "v8hKUB0lr2T1whNiiCpPDVGfyawLGq";
    public static String bucket = "taobaojiaju";

    public static boolean upload(String path, File file) {
        try {
            OSSClient ossClient = new OSSClient(OSSUtil.endpoint, OSSUtil.accessId, OSSUtil.accessKey);
            ossClient.putObject(OSSUtil.bucket, path + "/" + file.getName(), new FileInputStream(file));
            ossClient.shutdown();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean upload(byte[] content, String fileName) {
        try {
            OSSClient ossClient = new OSSClient(OSSUtil.endpoint, OSSUtil.accessId, OSSUtil.accessKey);
            ossClient.putObject(OSSUtil.bucket, fileName, new ByteArrayInputStream(content));
            ossClient.shutdown();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
