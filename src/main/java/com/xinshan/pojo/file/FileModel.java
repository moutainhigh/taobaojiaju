package com.xinshan.pojo.file;

import com.xinshan.utils.fileServer.FileUtils;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.im4java.core.IdentifyCmd;
import org.im4java.process.ArrayListOutputConsumer;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by xu on 15-9-8.
 */
public class FileModel {
    private String savePath;
    private String sourceFileName;
    private String fileMime;
    private Long fileSize;
    private String zoomFile;
    private String thumbnailFile;
    private String fileName;
    private String fileExtName;
    private Double width;
    private Double height;

    public FileModel(String savePath, String sourceFileName) {
        this.sourceFileName = savePath + sourceFileName;
        this.savePath = savePath;
        fileExtName = FileUtils.getExtName(this.sourceFileName);
        fileMime = FileUtils.getMime(this.sourceFileName);
        fileName = FileUtils.getName(this.sourceFileName);
        if (fileMime.contains("image")) {
            //todo it's image
            detail();
            if (width > 1024) {
                zoom(1024, null);
            } else if (height > 768) {
                zoom(null, 768);
            }
            thumbnail(128, 128);//图片缩略图
        }
    }

    private void detail() {
        try {
            IMOperation op = new IMOperation();
            op.addImage(1);
            IdentifyCmd identifyCmd = new IdentifyCmd(true);
            ArrayListOutputConsumer output = new ArrayListOutputConsumer();
            identifyCmd.setOutputConsumer(output);
            ArrayList<String> cmdOutput = output.getOutput();
            //高
            op.format("%h,%w");
            identifyCmd.run(op, sourceFileName);
            assert cmdOutput.size() == 1;
            String message = cmdOutput.get(0);
            String[] types = message.split(",");
            height = Double.parseDouble(types[0]);
            width = Double.parseDouble(types[1]);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     *
     * @param w
     * @param h
     */
    private void thumbnail(int w, int h) {
        thumbnailFile = fileName + "_" + w + "*" + h + "." + fileExtName;
        File file = new File(thumbnailFile);
        if (!file.exists()) {
            IMOperation op = new IMOperation();
            ConvertCmd cmd = new ConvertCmd(true);
            String thumb_size = w + "x" + h+"^";
            String extent = w + "x" + h;
            op.addRawArgs("-thumbnail", thumb_size);
            op.addRawArgs("-gravity", "center");
            op.addRawArgs("-extent", extent);
            op.addImage(sourceFileName);
            op.addImage(thumbnailFile);
            try {
                cmd.run(op);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void zoom(Integer w, Integer h) {
        zoomFile = fileName + "_" + w + "*" + h + "." + fileExtName;
        File file = new File(zoomFile);
        if (!file.exists()) {
            try {
                IMOperation op = new IMOperation();
                op.addImage(sourceFileName);
                if (width == null) {//根据高度缩放图片
                    op.resize(null, h);
                } else if (height == null) {//根据宽度缩放图片
                    op.resize(w, null);
                } else {
                    op.resize(w, h);
                }
                op.addImage(zoomFile);
                ConvertCmd convert = new ConvertCmd(true);
                convert.run(op);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public String getSourceFileName() {
        return sourceFileName;
    }

    public void setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }

    public String getFileMime() {
        return fileMime;
    }

    public void setFileMime(String fileMime) {
        this.fileMime = fileMime;
    }


    public String getZoomFile() {
        return zoomFile;
    }

    public void setZoomFile(String zoomFile) {
        this.zoomFile = zoomFile;
    }

    public String getThumbnailFile() {
        return thumbnailFile;
    }

    public void setThumbnailFile(String thumbnailFile) {
        this.thumbnailFile = thumbnailFile;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileExtName() {
        return fileExtName;
    }

    public void setFileExtName(String fileExtName) {
        this.fileExtName = fileExtName;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }


    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
}
