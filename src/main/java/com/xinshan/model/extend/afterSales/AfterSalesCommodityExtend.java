package com.xinshan.model.extend.afterSales;

import com.xinshan.model.*;

/**
 * Created by mxt on 16-12-15.
 */
public class AfterSalesCommodityExtend extends AfterSalesCommodity {
    private Supplier supplier;
    private Commodity commodity;
    private OrderCommodity orderCommodity;
    private OrderReturnCommodity orderReturnCommodity;
    private PurchaseCommodity purchaseCommodity;
    private SettlementCommodityPurchase settlementCommodityPurchase;

    public SettlementCommodityPurchase getSettlementCommodityPurchase() {
        return settlementCommodityPurchase;
    }

    public void setSettlementCommodityPurchase(SettlementCommodityPurchase settlementCommodityPurchase) {
        this.settlementCommodityPurchase = settlementCommodityPurchase;
    }

    public PurchaseCommodity getPurchaseCommodity() {
        return purchaseCommodity;
    }

    public void setPurchaseCommodity(PurchaseCommodity purchaseCommodity) {
        this.purchaseCommodity = purchaseCommodity;
    }

    public OrderReturnCommodity getOrderReturnCommodity() {
        return orderReturnCommodity;
    }

    public void setOrderReturnCommodity(OrderReturnCommodity orderReturnCommodity) {
        this.orderReturnCommodity = orderReturnCommodity;
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

    public Commodity getCommodity() {
        return commodity;
    }

    public void setCommodity(Commodity commodity) {
        this.commodity = commodity;
    }
}
