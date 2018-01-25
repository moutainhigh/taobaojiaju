package com.xinshan.model.extend.order;

import com.xinshan.model.*;
import com.xinshan.model.extend.activity.ActivityExtend;
import com.xinshan.model.extend.commodity.CommodityActivity;

import java.util.List;

/**
 * Created by mxt on 17-3-27.
 */
public class OrderReturnCommodityExtend extends OrderReturnCommodity {
    private Commodity commodity;
    private Supplier supplier;
    private AfterSalesCommodity afterSalesCommodity;
    private OrderCommodity orderCommodity;
    private PurchaseCommodity purchaseCommodity;
    private SettlementCommodityPurchase settlementCommodityPurchase;
    private OrderReturn orderReturn;
    private Order order;
    private CommodityActivity commodityActivity;
    private List<ActivityExtend> activities;//商品参与活动

    public CommodityActivity getCommodityActivity() {
        return commodityActivity;
    }

    public void setCommodityActivity(CommodityActivity commodityActivity) {
        this.commodityActivity = commodityActivity;
    }

    public List<ActivityExtend> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivityExtend> activities) {
        this.activities = activities;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public OrderReturn getOrderReturn() {
        return orderReturn;
    }

    public void setOrderReturn(OrderReturn orderReturn) {
        this.orderReturn = orderReturn;
    }

    public SettlementCommodityPurchase getSettlementCommodityPurchase() {
        return settlementCommodityPurchase;
    }

    public void setSettlementCommodityPurchase(SettlementCommodityPurchase settlementCommodityPurchase) {
        this.settlementCommodityPurchase = settlementCommodityPurchase;
    }

    public AfterSalesCommodity getAfterSalesCommodity() {
        return afterSalesCommodity;
    }

    public void setAfterSalesCommodity(AfterSalesCommodity afterSalesCommodity) {
        this.afterSalesCommodity = afterSalesCommodity;
    }

    public OrderCommodity getOrderCommodity() {
        return orderCommodity;
    }

    public void setOrderCommodity(OrderCommodity orderCommodity) {
        this.orderCommodity = orderCommodity;
    }

    public PurchaseCommodity getPurchaseCommodity() {
        return purchaseCommodity;
    }

    public void setPurchaseCommodity(PurchaseCommodity purchaseCommodity) {
        this.purchaseCommodity = purchaseCommodity;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Commodity getCommodity() {
        return commodity;
    }

    public void setCommodity(Commodity commodity) {
        this.commodity = commodity;
    }
}
