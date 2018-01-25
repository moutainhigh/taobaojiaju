package com.xinshan.model.extend.inventory;

import com.xinshan.model.*;
import com.xinshan.model.extend.activity.ActivityCommodityExtend;

/**
 * Created by mxt on 16-12-2.
 */
public class InventoryHistoryDetailExtend extends InventoryHistoryDetail {
    private CommodityNum commodityNum;
    private CommodityStore commodityStore;
    private InventoryInCommodity inventoryInCommodity;
    private Commodity commodity;
    private InventoryOutCommodity inventoryOutCommodity;
    private Supplier supplier;
    private OrderCommodity orderCommodity;
    private InventoryHistory inventoryHistory;
    private PurchaseCommodity purchaseCommodity;
    private Purchase purchase;
    private Order order;
    private ActivityCommodityExtend activityCommodityExtend;

    public ActivityCommodityExtend getActivityCommodityExtend() {
        return activityCommodityExtend;
    }

    public void setActivityCommodityExtend(ActivityCommodityExtend activityCommodityExtend) {
        this.activityCommodityExtend = activityCommodityExtend;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Purchase getPurchase() {
        return purchase;
    }

    public void setPurchase(Purchase purchase) {
        this.purchase = purchase;
    }

    public PurchaseCommodity getPurchaseCommodity() {
        return purchaseCommodity;
    }

    public void setPurchaseCommodity(PurchaseCommodity purchaseCommodity) {
        this.purchaseCommodity = purchaseCommodity;
    }

    public InventoryHistory getInventoryHistory() {
        return inventoryHistory;
    }

    public void setInventoryHistory(InventoryHistory inventoryHistory) {
        this.inventoryHistory = inventoryHistory;
    }

    public OrderCommodity getOrderCommodity() {
        return orderCommodity;
    }

    public void setOrderCommodity(OrderCommodity orderCommodity) {
        this.orderCommodity = orderCommodity;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public InventoryOutCommodity getInventoryOutCommodity() {
        return inventoryOutCommodity;
    }

    public void setInventoryOutCommodity(InventoryOutCommodity inventoryOutCommodity) {
        this.inventoryOutCommodity = inventoryOutCommodity;
    }

    public Commodity getCommodity() {
        return commodity;
    }

    public void setCommodity(Commodity commodity) {
        this.commodity = commodity;
    }

    public InventoryInCommodity getInventoryInCommodity() {
        return inventoryInCommodity;
    }

    public void setInventoryInCommodity(InventoryInCommodity inventoryInCommodity) {
        this.inventoryInCommodity = inventoryInCommodity;
    }

    public CommodityNum getCommodityNum() {
        return commodityNum;
    }

    public void setCommodityNum(CommodityNum commodityNum) {
        this.commodityNum = commodityNum;
    }

    public CommodityStore getCommodityStore() {
        return commodityStore;
    }

    public void setCommodityStore(CommodityStore commodityStore) {
        this.commodityStore = commodityStore;
    }
}
