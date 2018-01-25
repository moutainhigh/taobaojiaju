package com.xinshan.pojo.role;

import com.xinshan.pojo.SearchOption;

/**
 * Created by mxt on 17-5-5.
 */
public class DataPermitSearchOption extends SearchOption {
    private String url;
    private Integer role_id;

    public Integer getRole_id() {
        return role_id;
    }

    public void setRole_id(Integer role_id) {
        this.role_id = role_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
