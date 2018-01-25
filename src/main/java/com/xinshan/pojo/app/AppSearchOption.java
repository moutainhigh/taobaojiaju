package com.xinshan.pojo.app;

import com.xinshan.pojo.SearchOption;

import java.util.List;

/**
 * Created by mxt on 16-10-12.
 */
public class AppSearchOption extends SearchOption {
    private List<Integer> appIds;
    private List<Integer> buttonIds;

    public List<Integer> getAppIds() {
        return appIds;
    }

    public void setAppIds(List<Integer> appIds) {
        this.appIds = appIds;
    }

    public List<Integer> getButtonIds() {
        return buttonIds;
    }

    public void setButtonIds(List<Integer> buttonIds) {
        this.buttonIds = buttonIds;
    }
}
