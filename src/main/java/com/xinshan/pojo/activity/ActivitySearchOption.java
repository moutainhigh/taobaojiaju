package com.xinshan.pojo.activity;

import com.xinshan.pojo.SearchOption;

import java.util.Date;

/**
 * Created by mxt on 17-2-10.
 */
public class ActivitySearchOption extends SearchOption {
    private Integer activity_id;
    private Integer activity_commodity_id;
    private Integer activity_type;
    private Date currentDate;
    private Integer activity_status;
    private Integer activity_commodity_status;
    private Integer supplier_id;

    public Integer getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(Integer supplier_id) {
        this.supplier_id = supplier_id;
    }

    public Integer getActivity_commodity_status() {
        return activity_commodity_status;
    }

    public void setActivity_commodity_status(Integer activity_commodity_status) {
        this.activity_commodity_status = activity_commodity_status;
    }

    public Integer getActivity_status() {
        return activity_status;
    }

    public void setActivity_status(Integer activity_status) {
        this.activity_status = activity_status;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public Integer getActivity_type() {
        return activity_type;
    }

    public void setActivity_type(Integer activity_type) {
        this.activity_type = activity_type;
    }

    public Integer getActivity_commodity_id() {
        return activity_commodity_id;
    }

    public void setActivity_commodity_id(Integer activity_commodity_id) {
        this.activity_commodity_id = activity_commodity_id;
    }

    public Integer getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(Integer activity_id) {
        this.activity_id = activity_id;
    }
}
