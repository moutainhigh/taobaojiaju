package com.xinshan.controller.upload;

import com.aliyun.mns.common.utils.BinaryUtil;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.xinshan.model.Employee;
import com.xinshan.utils.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by mxt on 16-10-18.
 */
@Controller
public class UploadController {
    @RequestMapping("/upload")
    @ResponseBody
    public Set<String> springUpload(HttpServletRequest request) {
        Set<String> stringSet = new HashSet<String>();
        try {
            String pathDir = request.getSession().getServletContext().getRealPath("/WEB-INF/resources/tmp");
            CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
            if(multipartResolver.isMultipart(request)) {
                MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
                Iterator iterator = multiRequest.getFileNames();
                while (iterator.hasNext()) {
                    String key = iterator.next().toString();
                    MultipartFile file = multiRequest.getFile(key);
                    if (file != null) {
                        String filename = file.getOriginalFilename();
                        filename = fileExist(pathDir, filename);
                        String path = pathDir + "/" + filename;
                        file.transferTo(new File(path));
                        stringSet.add(filename);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stringSet;
    }
    /**
     * 文件存在时，重命名
     * @param pathDir
     * @param fileName
     */
    public String fileExist(String pathDir, String fileName) {
        File file = new File(pathDir+"/"+fileName);
        if (file.exists()) {
            String[] s = fileName.split("\\.");
            return s[0]+"1."+s[1];
        }else {
            return fileName;//60s 126k
        }
    }

    /**
     * oss上传文件回调
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/OSSSignature")
    public void OSSSignature(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employeeCode = RequestUtils.getRequestUtils().getEmployeeCode(request);
        String dir = employeeCode.getEmployee_code() + "/";
        String host = "http://" + OSSUtil.bucket + "." + OSSUtil.endpoint;
        OSSClient client = new OSSClient(OSSUtil.endpoint, OSSUtil.accessId, OSSUtil.accessKey);
        try {
            long expireTime = 30;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            String postPolicy = client.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = client.calculatePostSignature(postPolicy);

            Map<String, String> respMap = new LinkedHashMap<String, String>();
            respMap.put("accessid", OSSUtil.accessId);
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", dir);
            respMap.put("host", host);
            respMap.put("expire", String.valueOf(expireEndTime / 1000));
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST");
            ResponseUtil.sendSuccessResponse(request, response, respMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 商品图片上传
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/commodityFile/ossUpload")
    public void ossUpload(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = "commodity";
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        if(multipartResolver.isMultipart(request)) {
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
            Iterator iterator = multiRequest.getFileNames();
            while (iterator.hasNext()) {
                String key = iterator.next().toString();
                MultipartFile file = multiRequest.getFile(key);
                if (file != null) {
                    String filename = file.getOriginalFilename();
                    filename = URLEncoder.encode(filename, "UTF-8");
                    byte[] bytes = file.getBytes();
                    String filepath = path + "/" + EncryptionUtils.get32BitMD5(filename + "commodityOssUpload" + System.currentTimeMillis());
                    EncryptionUtils.get32BitMD5(filename);
                    if (OSSUtil.upload(bytes, filepath)) {
                        com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
                        jsonObject.put("filename", filename);
                        jsonObject.put("filepath", filepath);
                        jsonObject.put("filesize", bytes.length);
                        ResponseUtil.sendSuccessResponse(request, response, jsonObject);
                    }else {
                        ResponseUtil.sendResponse(request, response, new ResultData("0x0014", "上传失败！"));
                    }
                }
            }
        }
    }

    /**
     * oss文件下载
     * @param request
     * @param response
     * @throws IOException
     */ 
    @RequestMapping("/ossDownload")
    public void ossDownload(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String filename = request.getParameter("filename");
        String filepath = request.getParameter("filepath");

        OSSClient ossClient = new OSSClient(OSSUtil.endpoint, OSSUtil.accessId, OSSUtil.accessKey);
        // 读Object内容
        File file = new File(CommonUtils.FILE_DOWNLOAD_TMP + "/" + filename);
        ossClient.getObject(new GetObjectRequest(OSSUtil.bucket, filepath), file);
        ossClient.shutdown();
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(URLEncoder.encode(filename, "UTF-8"), "UTF-8"));
        response.setHeader("Cache-Control", "max-age=0");
        ServletOutputStream outputStream = response.getOutputStream();
        byte[] b=new byte[4096];
        int size = 0;
        if(file.exists()){
            FileInputStream fileInputStream=new FileInputStream(file);
            while ((size=fileInputStream.read(b))!=-1) {
                outputStream.write(b, 0, size);
            }
        }
        outputStream.flush();
    }
}
