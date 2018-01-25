package com.xinshan.model.extend.checking;

import com.xinshan.model.*;

/**
 * Created by mxt on 17-2-22.
 */
public class CheckingDetailExtend extends CheckingDetail {
    private Checking checking;
    private Order order;
    private Commodity commodity;
    private OrderCommodity orderCommodity;
    private CommoditySampleFix sampleFix;

    public CommoditySampleFix getSampleFix() {
        return sampleFix;
    }

    public void setSampleFix(CommoditySampleFix sampleFix) {
        this.sampleFix = sampleFix;
    }

    public OrderCommodity getOrderCommodity() {
        return orderCommodity;
    }

    public void setOrderCommodity(OrderCommodity orderCommodity) {
        this.orderCommodity = orderCommodity;
    }

    public Checking getChecking() {
        return checking;
    }

    public void setChecking(Checking checking) {
        this.checking = checking;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Commodity getCommodity() {
        return commodity;
    }

    public void setCommodity(Commodity commodity) {
        this.commodity = commodity;
    }
}
