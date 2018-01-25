package com.xinshan.model.extend.inventory;

import com.xinshan.model.*;

/**
 * Created by mxt on 16-11-10.
 */
public class InventoryInCommodityExtend extends InventoryInCommodity {
    private Commodity commodity;
    private PurchaseCommodity purchaseCommodity;
    private CommodityStore commodityStore;
    private OrderCommodity orderCommodity;

    public OrderCommodity getOrderCommodity() {
        return orderCommodity;
    }

    public void setOrderCommodity(OrderCommodity orderCommodity) {
        this.orderCommodity = orderCommodity;
    }

    public CommodityStore getCommodityStore() {
        return commodityStore;
    }

    public void setCommodityStore(CommodityStore commodityStore) {
        this.commodityStore = commodityStore;
    }

    public PurchaseCommodity getPurchaseCommodity() {
        return purchaseCommodity;
    }

    public void setPurchaseCommodity(PurchaseCommodity purchaseCommodity) {
        this.purchaseCommodity = purchaseCommodity;
    }

    public Commodity getCommodity() {
        return commodity;
    }

    public void setCommodity(Commodity commodity) {
        this.commodity = commodity;
    }
}
