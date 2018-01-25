package com.xinshan.model.extend.specialRightSell;

import com.xinshan.model.*;

import java.util.List;

/**
 * Created by mxt on 17-4-20.
 */
public class SpecialRightSellExtend extends SpecialRightSell {
    private Activity activity;
    private Order order;
    private ActivitySpecialRightCommodity activitySpecialRightCommodity;
    private Commodity commodity;
    private Supplier supplier;
    private List<OrderPay> orderPays;

    public ActivitySpecialRightCommodity getActivitySpecialRightCommodity() {
        return activitySpecialRightCommodity;
    }

    public void setActivitySpecialRightCommodity(ActivitySpecialRightCommodity activitySpecialRightCommodity) {
        this.activitySpecialRightCommodity = activitySpecialRightCommodity;
    }

    public Commodity getCommodity() {
        return commodity;
    }

    public void setCommodity(Commodity commodity) {
        this.commodity = commodity;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<OrderPay> getOrderPays() {
        return orderPays;
    }

    public void setOrderPays(List<OrderPay> orderPays) {
        this.orderPays = orderPays;
    }
}
