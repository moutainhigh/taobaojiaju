package com.xinshan.model.extend.purchase;

import com.xinshan.model.*;
import com.xinshan.model.extend.activity.ActivityCommodityExtend;
import com.xinshan.model.extend.commodity.CommodityNumExtend;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by mxt on 16-11-2.
 */
public class PurchaseCommodityExtend extends PurchaseCommodity {
    private Commodity commodity;
    private BigDecimal order_commodity_price;//订单商品价格
    private List<CommodityNumExtend> commodityNumList;//库存列表
    private OrderCommodity orderCommodity;
    private Supplier supplier;
    private ActivityCommodityExtend activityCommodity;
    private SettlementCommodityPurchase settlementCommodityPurchase;

    public SettlementCommodityPurchase getSettlementCommodityPurchase() {
        return settlementCommodityPurchase;
    }

    public void setSettlementCommodityPurchase(SettlementCommodityPurchase settlementCommodityPurchase) {
        this.settlementCommodityPurchase = settlementCommodityPurchase;
    }

    public ActivityCommodityExtend getActivityCommodity() {
        return activityCommodity;
    }

    public void setActivityCommodity(ActivityCommodityExtend activityCommodity) {
        this.activityCommodity = activityCommodity;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public OrderCommodity getOrderCommodity() {
        return orderCommodity;
    }

    public void setOrderCommodity(OrderCommodity orderCommodity) {
        this.orderCommodity = orderCommodity;
    }

    public List<CommodityNumExtend> getCommodityNumList() {
        return commodityNumList;
    }

    public void setCommodityNumList(List<CommodityNumExtend> commodityNumList) {
        this.commodityNumList = commodityNumList;
    }

    public BigDecimal getOrder_commodity_price() {
        return order_commodity_price;
    }

    public void setOrder_commodity_price(BigDecimal order_commodity_price) {
        this.order_commodity_price = order_commodity_price;
    }

    public Commodity getCommodity() {
        return commodity;
    }

    public void setCommodity(Commodity commodity) {
        this.commodity = commodity;
    }
}
