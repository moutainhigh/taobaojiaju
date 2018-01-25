package com.xinshan.pojo.fastdfs;

/**
 * Created by jonson.xu on 6/24/15.
 */
public class FileModel {
    private byte[] content;
    private String mime;
    private String filename;
    private Long size;
    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}
