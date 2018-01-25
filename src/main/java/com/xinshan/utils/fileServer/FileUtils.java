package com.xinshan.utils.fileServer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xinshan.utils.CommonUtils;
import com.xinshan.pojo.fastdfs.ResultFileInfo;

import javax.activation.MimetypesFileTypeMap;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jonson.xu on 6/25/15.
 */
public class FileUtils {
    /**
     * 获取文件的扩展名
     * @param fileName
     * @return
     */
    public static String getExtName(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            int dotPosition = fileName.lastIndexOf(".");
            if (dotPosition + 1 < fileName.length()) {
                return fileName.substring(dotPosition + 1);
            }
        }
        return null;
    }

    /**
     * 获取文件的MIME
     * @param filename
     * @return
     */
    public static String getMime(String filename){
        if (filename!=null){
            try {
                MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap(CommonUtils.META_INF_PATH+"/mime.types");
                return mimetypesFileTypeMap.getContentType(filename);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static String getName(String filename){
        if (filename != null && filename.contains(".")) {
            int dotPosition = filename.lastIndexOf(".");
            if (dotPosition + 1 < filename.length()) {
                return filename.substring(0,dotPosition - 1);
            }
        }
        return null;
    }
    /**
     *
     * @param resultFileInfo
     * @param object
     * @param namespace
     * @return
     */
    public static String getAttachment(ResultFileInfo resultFileInfo, String object, String namespace) {
        List<ResultFileInfo> list = new ArrayList<ResultFileInfo>();
        list.add(resultFileInfo);
        return getAttachment(list, object, namespace);
    }

    /**
     *
     * @param list
     * @param object
     * @param namespace
     * @return
     */
    public static String getAttachment(List<ResultFileInfo> list, String object, String namespace) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("object", object);
        jsonObject.put("namespace", namespace);
        JSONArray array = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            ResultFileInfo resultFileInfo = list.get(i);
            String fileName = resultFileInfo.getFilename();
            long fileSize = resultFileInfo.getFilesize();
            String key = resultFileInfo.getKey();
            JSONObject attachment = new JSONObject();
            attachment.put("filesize", fileSize);
            attachment.put("filename", fileName);
            attachment.put("key", key);
            array.add(attachment);
        }
        jsonObject.put("attachment", array);
        return jsonObject.toString();
    }
}
