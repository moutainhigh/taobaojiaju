package com.xinshan.model.extend.role;

import com.xinshan.model.Role;

import java.util.List;

/**
 * Created by mxt on 16-10-13.
 */
public class RoleExtend extends Role {
    private String app_titles;
    private String button_titles;
    private List<RoleExtend> list;
    private String parentRoleName;

    public String getParentRoleName() {
        return parentRoleName;
    }

    public void setParentRoleName(String parentRoleName) {
        this.parentRoleName = parentRoleName;
    }

    public List<RoleExtend> getList() {
        return list;
    }

    public void setList(List<RoleExtend> list) {
        this.list = list;
    }

    public String getApp_titles() {
        return app_titles;
    }

    public void setApp_titles(String app_titles) {
        this.app_titles = app_titles;
    }

    public String getButton_titles() {
        return button_titles;
    }

    public void setButton_titles(String button_titles) {
        this.button_titles = button_titles;
    }
}
