package com.xinshan.pojo.orderFee;

import com.xinshan.pojo.SearchOption;

import java.util.List;

/**
 * Created by mxt on 17-4-24.
 */
public class OrderFeeSearchOption extends SearchOption {
    private List<Integer> orderFeeIds;
    private Integer order_fee_id;
    private Integer after_sales_id;
    private Integer inventory_history_id;
    private Integer supplier_id;

    public Integer getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(Integer supplier_id) {
        this.supplier_id = supplier_id;
    }

    public Integer getInventory_history_id() {
        return inventory_history_id;
    }

    public void setInventory_history_id(Integer inventory_history_id) {
        this.inventory_history_id = inventory_history_id;
    }

    public Integer getAfter_sales_id() {
        return after_sales_id;
    }

    public void setAfter_sales_id(Integer after_sales_id) {
        this.after_sales_id = after_sales_id;
    }

    public List<Integer> getOrderFeeIds() {
        return orderFeeIds;
    }

    public void setOrderFeeIds(List<Integer> orderFeeIds) {
        this.orderFeeIds = orderFeeIds;
    }

    public Integer getOrder_fee_id() {
        return order_fee_id;
    }

    public void setOrder_fee_id(Integer order_fee_id) {
        this.order_fee_id = order_fee_id;
    }
}
