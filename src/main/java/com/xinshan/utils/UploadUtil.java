package com.xinshan.utils;

import com.xinshan.model.Employee;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * Created by mxt on 17-4-19.
 */
public class UploadUtil {

    /**
     * 上传导入文件保存
     * @param file
     * @param fileName
     */
    public static void saveFile(MultipartFile file, String fileName, Employee employee, String url) {
        try {
            /*byte[] bytes = fileName.getBytes(Charset.forName("gbk"));
            fileName = new String(bytes, "utf-8");*/
            fileName =  employee.getEmployee_name()+ "("+ url.replaceAll("/", "-") +"-" +DateUtil.format(DateUtil.currentDate(), "yy-MM-dd HH:mm:ss") + ")" + fileName;
            File file1 = new File(CommonUtils.FILE_UPLOAD_DIR + "/" + fileName);
            file.transferTo(file1);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
