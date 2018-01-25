package com.xinshan.utils;

/**
 * Created by mxt on 16-10-9.
 */
public class ResultData {
    private String code;
    private Object data;
    private String postData;
    private int currentTime;

    /**
     *
     * @param code
     * @param data
     * @param postData
     */
    public ResultData(String code, Object data, String postData) {
        this.code = code;
        this.data = data;
        this.postData = postData;
        this.currentTime = (int)(DateUtil.currentDate().getTime()/1000);
    }

    public ResultData(String code, Object data) {
        this.code = code;
        this.data = data;
        this.currentTime = (int)(DateUtil.currentDate().getTime()/1000);
    }

    public ResultData(String code) {
        this.code = code;
        this.data = "";
        this.currentTime = (int)(DateUtil.currentDate().getTime()/1000);
    }

    public String getPostData() {
        return postData;
    }

    public void setPostData(String postData) {
        this.postData = postData;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(int currentTime) {
        this.currentTime = currentTime;
    }
}
