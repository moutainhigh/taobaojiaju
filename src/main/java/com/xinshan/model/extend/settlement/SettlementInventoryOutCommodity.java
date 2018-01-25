package com.xinshan.model.extend.settlement;

import com.xinshan.model.*;


/**
 * Created by mxt on 16-12-20.
 */
public class SettlementInventoryOutCommodity extends InventoryHistoryDetail {
    private OrderCommodity orderCommodity;
    private Commodity commodity;
    private PurchaseCommodity purchaseCommodity;
    private ActivityCommodity activityCommodity;
    private Activity activity;
    private InventoryOutCommodity inventoryOutCommodity;
    private InventoryOut inventoryOut;
    private SettlementCommodityPurchase settlementCommodityPurchase;
    private InventoryInCommodity inventoryInCommodity;
    private GiftCommodity giftCommodity;

    public GiftCommodity getGiftCommodity() {
        return giftCommodity;
    }

    public void setGiftCommodity(GiftCommodity giftCommodity) {
        this.giftCommodity = giftCommodity;
    }

    public InventoryInCommodity getInventoryInCommodity() {
        return inventoryInCommodity;
    }

    public void setInventoryInCommodity(InventoryInCommodity inventoryInCommodity) {
        this.inventoryInCommodity = inventoryInCommodity;
    }

    public SettlementCommodityPurchase getSettlementCommodityPurchase() {
        return settlementCommodityPurchase;
    }

    public void setSettlementCommodityPurchase(SettlementCommodityPurchase settlementCommodityPurchase) {
        this.settlementCommodityPurchase = settlementCommodityPurchase;
    }

    public InventoryOutCommodity getInventoryOutCommodity() {
        return inventoryOutCommodity;
    }

    public void setInventoryOutCommodity(InventoryOutCommodity inventoryOutCommodity) {
        this.inventoryOutCommodity = inventoryOutCommodity;
    }

    public InventoryOut getInventoryOut() {
        return inventoryOut;
    }

    public void setInventoryOut(InventoryOut inventoryOut) {
        this.inventoryOut = inventoryOut;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public ActivityCommodity getActivityCommodity() {
        return activityCommodity;
    }

    public void setActivityCommodity(ActivityCommodity activityCommodity) {
        this.activityCommodity = activityCommodity;
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

    public OrderCommodity getOrderCommodity() {
        return orderCommodity;
    }

    public void setOrderCommodity(OrderCommodity orderCommodity) {
        this.orderCommodity = orderCommodity;
    }
}
