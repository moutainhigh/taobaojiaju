package com.xinshan.model.extend.gift;

import com.xinshan.model.*;

/**
 * Created by mxt on 17-4-15.
 */
public class GiftCommodityExtend extends GiftCommodity {
    private Gift gift;
    private Commodity commodity;
    private Supplier supplier;
    private CommodityNum commodityNum;
    private CommodityStore commodityStore;
    private SettlementCommodityPurchase settlementCommodityPurchase;
    private Activity activity;
    private User user;
    private Order order;
    private PurchaseCommodity purchaseCommodity;

    public PurchaseCommodity getPurchaseCommodity() {
        return purchaseCommodity;
    }

    public void setPurchaseCommodity(PurchaseCommodity purchaseCommodity) {
        this.purchaseCommodity = purchaseCommodity;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Gift getGift() {
        return gift;
    }

    public void setGift(Gift gift) {
        this.gift = gift;
    }

    public SettlementCommodityPurchase getSettlementCommodityPurchase() {
        return settlementCommodityPurchase;
    }

    public void setSettlementCommodityPurchase(SettlementCommodityPurchase settlementCommodityPurchase) {
        this.settlementCommodityPurchase = settlementCommodityPurchase;
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
}
