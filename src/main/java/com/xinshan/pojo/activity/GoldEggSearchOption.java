package com.xinshan.pojo.activity;

import com.xinshan.pojo.SearchOption;

/**
 * Created by mxt on 17-4-18.
 */
public class GoldEggSearchOption extends SearchOption {
    private Integer activity_id;
    private Integer order_id;
    private Integer user_id;

    public Integer getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(Integer activity_id) {
        this.activity_id = activity_id;
    }

    public Integer getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }
}
