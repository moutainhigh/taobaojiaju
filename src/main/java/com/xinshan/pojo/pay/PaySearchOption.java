package com.xinshan.pojo.pay;

import com.xinshan.model.OrderPay;
import com.xinshan.pojo.SearchOption;

import java.util.List;

/**
 * Created by mxt on 17-4-14.
 */
public class PaySearchOption extends SearchOption {
    private List<Integer> orderPayIds;
    private List<Integer> orderPayReturnIds;
    private Integer order_id;
    private Integer order_return_id;
    private Integer order_pay_type;
    private Integer pay_source;

    public Integer getOrder_pay_type() {
        return order_pay_type;
    }

    public void setOrder_pay_type(Integer order_pay_type) {
        this.order_pay_type = order_pay_type;
    }

    public Integer getPay_source() {
        return pay_source;
    }

    public void setPay_source(Integer pay_source) {
        this.pay_source = pay_source;
    }

    public List<Integer> getOrderPayReturnIds() {
        return orderPayReturnIds;
    }

    public void setOrderPayReturnIds(List<Integer> orderPayReturnIds) {
        this.orderPayReturnIds = orderPayReturnIds;
    }

    public Integer getOrder_return_id() {
        return order_return_id;
    }

    public void setOrder_return_id(Integer order_return_id) {
        this.order_return_id = order_return_id;
    }

    public Integer getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
    }

    public List<Integer> getOrderPayIds() {
        return orderPayIds;
    }

    public void setOrderPayIds(List<Integer> orderPayIds) {
        this.orderPayIds = orderPayIds;
    }
}
