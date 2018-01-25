package com.xinshan.model.extend.settlement;

import com.xinshan.model.*;
import com.xinshan.model.extend.afterSales.AfterSalesCommodityExtend;
import com.xinshan.model.extend.gift.GiftCommodityExtend;
import com.xinshan.model.extend.gift.GiftReturnCommodityExtend;
import com.xinshan.model.extend.order.OrderReturnCommodityExtend;
import com.xinshan.model.extend.orderFee.OrderFeeExtend;
import com.xinshan.model.extend.purchase.PurchaseCommodityExtend;

import java.util.List;

/**
 * Created by mxt on 16-11-23.
 */
public class SettlementExtend extends Settlement {
    private Supplier supplier;
    private Order order;
    private List<SettlementInventoryOutCommodity> settlementCommodities;
    private List<OrderFeeExtend> orderFees;
    private List<AfterSalesCommodityExtend> afterSalesCommodities;
    private List<SettlementHistory> settlementHistories;
    private InventoryOut inventoryOut;
    private List<OrderReturnCommodityExtend> orderReturnCommodities;
    private InventoryHistory inventoryHistory;
    private List<GiftCommodityExtend> giftCommodityExtendList;
    private List<GiftReturnCommodityExtend> giftReturnCommodities;
    private CommoditySampleFix commoditySampleFix;
    private GiftReturnCommodityExtend giftReturnCommodity;
    private Gift gift;
    private PurchaseCommodityExtend purchaseCommodity;

    public PurchaseCommodityExtend getPurchaseCommodity() {
        return purchaseCommodity;
    }

    public void setPurchaseCommodity(PurchaseCommodityExtend purchaseCommodity) {
        this.purchaseCommodity = purchaseCommodity;
    }

    public Gift getGift() {
        return gift;
    }

    public void setGift(Gift gift) {
        this.gift = gift;
    }

    public GiftReturnCommodityExtend getGiftReturnCommodity() {
        return giftReturnCommodity;
    }

    public void setGiftReturnCommodity(GiftReturnCommodityExtend giftReturnCommodity) {
        this.giftReturnCommodity = giftReturnCommodity;
    }

    public CommoditySampleFix getCommoditySampleFix() {
        return commoditySampleFix;
    }

    public void setCommoditySampleFix(CommoditySampleFix commoditySampleFix) {
        this.commoditySampleFix = commoditySampleFix;
    }

    public List<GiftReturnCommodityExtend> getGiftReturnCommodities() {
        return giftReturnCommodities;
    }

    public void setGiftReturnCommodities(List<GiftReturnCommodityExtend> giftReturnCommodities) {
        this.giftReturnCommodities = giftReturnCommodities;
    }

    public List<GiftCommodityExtend> getGiftCommodityExtendList() {
        return giftCommodityExtendList;
    }

    public void setGiftCommodityExtendList(List<GiftCommodityExtend> giftCommodityExtendList) {
        this.giftCommodityExtendList = giftCommodityExtendList;
    }

    public InventoryHistory getInventoryHistory() {
        return inventoryHistory;
    }

    public void setInventoryHistory(InventoryHistory inventoryHistory) {
        this.inventoryHistory = inventoryHistory;
    }

    public List<OrderReturnCommodityExtend> getOrderReturnCommodities() {
        return orderReturnCommodities;
    }

    public void setOrderReturnCommodities(List<OrderReturnCommodityExtend> orderReturnCommodities) {
        this.orderReturnCommodities = orderReturnCommodities;
    }

    public InventoryOut getInventoryOut() {
        return inventoryOut;
    }

    public void setInventoryOut(InventoryOut inventoryOut) {
        this.inventoryOut = inventoryOut;
    }

    public List<SettlementHistory> getSettlementHistories() {
        return settlementHistories;
    }

    public void setSettlementHistories(List<SettlementHistory> settlementHistories) {
        this.settlementHistories = settlementHistories;
    }

    public List<AfterSalesCommodityExtend> getAfterSalesCommodities() {
        return afterSalesCommodities;
    }

    public void setAfterSalesCommodities(List<AfterSalesCommodityExtend> afterSalesCommodities) {
        this.afterSalesCommodities = afterSalesCommodities;
    }

    public List<OrderFeeExtend> getOrderFees() {
        return orderFees;
    }

    public void setOrderFees(List<OrderFeeExtend> orderFees) {
        this.orderFees = orderFees;
    }

    public List<SettlementInventoryOutCommodity> getSettlementCommodities() {
        return settlementCommodities;
    }

    public void setSettlementCommodities(List<SettlementInventoryOutCommodity> settlementCommodities) {
        this.settlementCommodities = settlementCommodities;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

}
