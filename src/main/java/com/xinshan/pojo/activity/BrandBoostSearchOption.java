package com.xinshan.pojo.activity;

import com.xinshan.pojo.SearchOption;

/**
 * Created by mxt on 17-4-19.
 */
public class BrandBoostSearchOption extends SearchOption {
    private Integer user_id;
    private Integer activity_id;
    private Integer brand_boost_id;

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(Integer activity_id) {
        this.activity_id = activity_id;
    }

    public Integer getBrand_boost_id() {
        return brand_boost_id;
    }

    public void setBrand_boost_id(Integer brand_boost_id) {
        this.brand_boost_id = brand_boost_id;
    }
}
