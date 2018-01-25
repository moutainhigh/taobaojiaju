package com.xinshan.controller.download;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by mxt on 15-8-20.
 */
@Controller
public class DownloadController {
    //    "{"namespace":"sw_notice","object":"notice589","attachment":[{"key":"YTk4OTYxODk4NGIwMjA4MzM4MDIwYzFlYjk5NzNiOGI=",
// "filename":"\u5173\u4e8e\u53ec\u5f00\u51e4\u51f0\u7269\u4e1a\u516c\u53f8\u65b0\u5458\u5de5\u5ea7\u8c08\u4f1a\u7684\u901a\u77e5.docx","filesize":14933}]}"
    //    http://192.168.5.100/file/download/sw_chat_message/message27991/NWU5MzJiYWQyNzIyNDlmNzEwNDhkYTM3Y2RlNjE4YTc=
    @RequestMapping("/file/download/{namespace}/{object}/{key}")
    public void chatFujianDownload(@PathVariable("namespace") String namespace, @PathVariable("object") String object
            , @PathVariable("key") String key, HttpServletResponse response) throws IOException {
        /*FastDFSUtils fastDFSUtils = new FastDFSUtils(namespace, object);
        FileModel fileModel = fastDFSUtils.download(key);
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileModel.getFileName(), "UTF-8"));
        response.addHeader("Content-Length", fileModel.getFileSize() + "");
        response.setHeader("Content-Type", fileModel.getFileMime());
        response.setHeader("Cache-Control", "max-age=0");
        File file=new File(fileModel.getSourceFileName());
        OutputStream outputStream = response.getOutputStream();
        byte[] b=new byte[4096];
        int size = 0;
        if(file.exists()){
            FileInputStream fileInputStream=new FileInputStream(file);
            while ((size=fileInputStream.read(b))!=-1) {
                outputStream.write(b, 0, size);
            }
        }
        outputStream.flush();*/
    }

    @RequestMapping("/file/downloadTmp")
    public void downloadTmpFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String filename = request.getParameter("filename");
        String pathDir = request.getSession().getServletContext().getRealPath("/WEB-INF/resources/tmp");
        File file=new File(pathDir+"/"+filename);
        OutputStream outputStream = response.getOutputStream();
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

    /**
     * 图片文件预览
     *
     * @param namespace
     * @param object
     * @param key
     * @param response
     * @throws IOException
     */
    @RequestMapping("/file/thumbnails/{namespace}/{object}/{key}")
    public void fileThumbnails(@PathVariable("namespace") String namespace, @PathVariable("object") String object
            , @PathVariable("key") String key, HttpServletResponse response) throws IOException {
        /*FastDFSUtils fastDFSUtils = new FastDFSUtils(namespace, object);
        FileModel fileModel = fastDFSUtils.download(key);
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileModel.getFileName(), "UTF-8"));
        //response.addHeader("Content-Length", fileModel.getFileSize() + "");
        response.setHeader("Content-Type", fileModel.getFileMime());
        response.setHeader("Cache-Control", "max-age=0");
        File file=new File(fileModel.getThumbnailFile());
        OutputStream outputStream = response.getOutputStream();
        byte[] b=new byte[4096];
        int size = 0;
        if(file.exists()){
            FileInputStream fileInputStream=new FileInputStream(file);
            while ((size=fileInputStream.read(b))!=-1) {
                outputStream.write(b, 0, size);
            }
        }
        outputStream.flush();*/
    }
}
