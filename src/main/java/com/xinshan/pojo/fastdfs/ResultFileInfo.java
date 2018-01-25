package com.xinshan.pojo.fastdfs;

/**
 * Created by xu on 15-8-12.
 */
public class ResultFileInfo {
    private Long filesize;
    private String filename;
    private String key;

    public Long getFilesize() {
        return filesize;
    }

    public void setFilesize(Long filesize) {
        this.filesize = filesize;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
