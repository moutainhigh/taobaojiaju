package com.xinshan.model.extend.orderFee;

import com.xinshan.model.*;

/**
 * Created by mxt on 16-11-23.
 */
public class OrderFeeExtend extends OrderFee {
    private OrderFeeType orderFeeType;
    private Supplier supplier;
    private Worker worker;
    private Order order;
    private String sampleFixRemark;
    private String sampleFixCode;

    public String getSampleFixCode() {
        return sampleFixCode;
    }

    public void setSampleFixCode(String sampleFixCode) {
        this.sampleFixCode = sampleFixCode;
    }

    public String getSampleFixRemark() {
        return sampleFixRemark;
    }

    public void setSampleFixRemark(String sampleFixRemark) {
        this.sampleFixRemark = sampleFixRemark;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Worker getWorker() {
        return worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public OrderFeeType getOrderFeeType() {
        return orderFeeType;
    }

    public void setOrderFeeType(OrderFeeType orderFeeType) {
        this.orderFeeType = orderFeeType;
    }
}
