package com.xinshan.pojo.activity;

import com.xinshan.pojo.SearchOption;

import java.util.List;

/**
 * Created by mxt on 17-4-17.
 */
public class CashBackSearchOption extends SearchOption {
    private List<Integer> cashBackIds;
    private Integer cash_back_id;
    private Integer activity_id;
    private Integer order_id;
    private Integer order_return_id;
    private Integer cash_back_type;

    public Integer getCash_back_type() {
        return cash_back_type;
    }

    public void setCash_back_type(Integer cash_back_type) {
        this.cash_back_type = cash_back_type;
    }

    public Integer getOrder_return_id() {
        return order_return_id;
    }

    public void setOrder_return_id(Integer order_return_id) {
        this.order_return_id = order_return_id;
    }

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

    public Integer getCash_back_id() {
        return cash_back_id;
    }

    public void setCash_back_id(Integer cash_back_id) {
        this.cash_back_id = cash_back_id;
    }

    public List<Integer> getCashBackIds() {
        return cashBackIds;
    }

    public void setCashBackIds(List<Integer> cashBackIds) {
        this.cashBackIds = cashBackIds;
    }
}
