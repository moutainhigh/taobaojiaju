package com.xinshan.pojo.afterSales;

import com.xinshan.pojo.SearchOption;

import java.util.List;

/**
 * Created by mxt on 16-12-12.
 */
public class AfterSalesSearchOption extends SearchOption {
    private Integer order_fee_id;
    private Integer order_fee_type_id;
    private Integer inventory_history_id;
    private Integer worker_id;
    private Integer supplier_id;
    private Integer order_id;
    private List<Integer> afterSalesIds;
    private Integer commodity_id;
    private Integer after_sales_id;
    private Integer after_sales_commodity_id;
    private Integer after_sales_type;
    private String order_fee_source;
    private Integer order_fee_enable;
    private Integer fee;//1有费用，2没有费用

    public Integer getFee() {
        return fee;
    }

    public void setFee(Integer fee) {
        this.fee = fee;
    }

    public Integer getOrder_fee_enable() {
        return order_fee_enable;
    }

    public void setOrder_fee_enable(Integer order_fee_enable) {
        this.order_fee_enable = order_fee_enable;
    }

    public String getOrder_fee_source() {
        return order_fee_source;
    }

    public void setOrder_fee_source(String order_fee_source) {
        this.order_fee_source = order_fee_source;
    }

    public Integer getAfter_sales_type() {
        return after_sales_type;
    }

    public void setAfter_sales_type(Integer after_sales_type) {
        this.after_sales_type = after_sales_type;
    }

    public Integer getAfter_sales_commodity_id() {
        return after_sales_commodity_id;
    }

    public void setAfter_sales_commodity_id(Integer after_sales_commodity_id) {
        this.after_sales_commodity_id = after_sales_commodity_id;
    }

    public Integer getAfter_sales_id() {
        return after_sales_id;
    }

    public void setAfter_sales_id(Integer after_sales_id) {
        this.after_sales_id = after_sales_id;
    }

    public Integer getCommodity_id() {
        return commodity_id;
    }

    public void setCommodity_id(Integer commodity_id) {
        this.commodity_id = commodity_id;
    }

    public List<Integer> getAfterSalesIds() {
        return afterSalesIds;
    }

    public void setAfterSalesIds(List<Integer> afterSalesIds) {
        this.afterSalesIds = afterSalesIds;
    }

    public Integer getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
    }

    public Integer getOrder_fee_id() {
        return order_fee_id;
    }

    public void setOrder_fee_id(Integer order_fee_id) {
        this.order_fee_id = order_fee_id;
    }

    public Integer getOrder_fee_type_id() {
        return order_fee_type_id;
    }

    public void setOrder_fee_type_id(Integer order_fee_type_id) {
        this.order_fee_type_id = order_fee_type_id;
    }

    public Integer getInventory_history_id() {
        return inventory_history_id;
    }

    public void setInventory_history_id(Integer inventory_history_id) {
        this.inventory_history_id = inventory_history_id;
    }

    public Integer getWorker_id() {
        return worker_id;
    }

    public void setWorker_id(Integer worker_id) {
        this.worker_id = worker_id;
    }

    public Integer getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(Integer supplier_id) {
        this.supplier_id = supplier_id;
    }
}
