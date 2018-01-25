package com.xinshan.pojo.salesTarget;

import com.xinshan.pojo.SearchOption;

import java.util.List;

/**
 * Created by mxt on 17-8-22.
 */
public class SalesTargetSearchOption extends SearchOption{
    private Integer sales_target_year;
    private Integer sales_target_month;
    private List<Integer> salesTargetIds;
    private Integer position_id;

    public Integer getSales_target_month() {
        return sales_target_month;
    }

    public void setSales_target_month(Integer sales_target_month) {
        this.sales_target_month = sales_target_month;
    }

    public Integer getPosition_id() {
        return position_id;
    }

    public void setPosition_id(Integer position_id) {
        this.position_id = position_id;
    }

    public List<Integer> getSalesTargetIds() {
        return salesTargetIds;
    }

    public void setSalesTargetIds(List<Integer> salesTargetIds) {
        this.salesTargetIds = salesTargetIds;
    }

    public Integer getSales_target_year() {
        return sales_target_year;
    }

    public void setSales_target_year(Integer sales_target_year) {
        this.sales_target_year = sales_target_year;
    }
}
