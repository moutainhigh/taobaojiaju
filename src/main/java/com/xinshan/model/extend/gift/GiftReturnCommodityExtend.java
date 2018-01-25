package com.xinshan.model.extend.gift;

import com.xinshan.model.*;

/**
 * Created by mxt on 17-5-26.
 */
public class GiftReturnCommodityExtend extends GiftReturnCommodity {
    private GiftReturn giftReturn;
    private GiftCommodity giftCommodity;
    private Gift gift;
    private Commodity commodity;
    private Supplier supplier;
    private CommodityNum commodityNum;
    private CommodityStore commodityStore;
    private Activity activity;
    private User user;
    private Order order;
    private PurchaseCommodity purchaseCommodity;
    private SettlementCommodityPurchase settlementCommodityPurchase;

    public Gift getGift() {
        return gift;
    }

    public void setGift(Gift gift) {
        this.gift = gift;
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

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public PurchaseCommodity getPurchaseCommodity() {
        return purchaseCommodity;
    }

    public void setPurchaseCommodity(PurchaseCommodity purchaseCommodity) {
        this.purchaseCommodity = purchaseCommodity;
    }

    public GiftReturn getGiftReturn() {
        return giftReturn;
    }

    public void setGiftReturn(GiftReturn giftReturn) {
        this.giftReturn = giftReturn;
    }

    public SettlementCommodityPurchase getSettlementCommodityPurchase() {
        return settlementCommodityPurchase;
    }

    public void setSettlementCommodityPurchase(SettlementCommodityPurchase settlementCommodityPurchase) {
        this.settlementCommodityPurchase = settlementCommodityPurchase;
    }

    public GiftCommodity getGiftCommodity() {
        return giftCommodity;
    }

    public void setGiftCommodity(GiftCommodity giftCommodity) {
        this.giftCommodity = giftCommodity;
    }

}
