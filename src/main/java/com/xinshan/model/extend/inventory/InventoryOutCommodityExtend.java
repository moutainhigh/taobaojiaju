package com.xinshan.model.extend.inventory;

import com.xinshan.model.*;
import com.xinshan.model.extend.activity.ActivityCommodityExtend;
import com.xinshan.model.extend.commodity.CommodityNumExtend;

import java.util.List;

/**
 * Created by mxt on 16-11-11.
 */
public class InventoryOutCommodityExtend extends InventoryOutCommodity {
    private Commodity commodity;
    private Supplier supplier;
    private Integer inventory_num;//库存总数
    private OrderCommodity orderCommodity;
    private List<CommodityNumExtend> commodityNa;
    private GiftCommodity giftCommodity;
    private ActivityCommodityExtend orderCommodityActivity;

    public ActivityCommodityExtend getOrderCommodityActivity() {
        return orderCommodityActivity;
    }

    public void setOrderCommodityActivity(ActivityCommodityExtend orderCommodityActivity) {
        this.orderCommodityActivity = orderCommodityActivity;
    }

    public GiftCommodity getGiftCommodity() {
        return giftCommodity;
    }

    public void setGiftCommodity(GiftCommodity giftCommodity) {
        this.giftCommodity = giftCommodity;
    }

    public List<CommodityNumExtend> getCommodityNa() {
        return commodityNa;
    }

    public void setCommodityNa(List<CommodityNumExtend> commodityNa) {
        this.commodityNa = commodityNa;
    }

    public OrderCommodity getOrderCommodity() {
        return orderCommodity;
    }

    public void setOrderCommodity(OrderCommodity orderCommodity) {
        this.orderCommodity = orderCommodity;
    }

    public Integer getInventory_num() {
        return inventory_num;
    }

    public void setInventory_num(Integer inventory_num) {
        this.inventory_num = inventory_num;
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
