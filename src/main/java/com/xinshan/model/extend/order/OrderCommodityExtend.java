package com.xinshan.model.extend.order;

import com.xinshan.model.*;
import com.xinshan.model.extend.activity.ActivityCommodityExtend;
import com.xinshan.model.extend.activity.ActivityExtend;
import com.xinshan.model.extend.commodity.CommodityActivity;
import com.xinshan.utils.DateUtil;

import java.util.Date;
import java.util.List;

/**
 * Created by mxt on 16-10-26.
 */
public class OrderCommodityExtend extends OrderCommodity {
    private OrderCommodityValueAddedCard orderCommodityValueAddedCard;
    private CashBackCommodity cashBackCommodity;
    private Commodity commodity;
    private Supplier supplier;
    private CommodityStore commodityStore;
    private int arrivalNum;
    private Order order;
    private PurchaseCommodity purchaseCommodity;
    private ActivityCommodityExtend activityCommodity;
    private Position position;
    private CommodityActivity commodityActivity;
    private List<ActivityExtend> activities;//商品参与活动
    private InventoryOutCommodity inventoryOutCommodity;
    private CommodityActivity sellLimit;
    private OrderCommoditySupplier orderCommoditySupplier;

    private int delivery;//送货倒计时

    public int getDelivery() {
        if (order != null && order.getDelivery_date() != null) {
            Date date = DateUtil.currentDate();
            Date delivery_date = order.getDelivery_date();
            delivery = DateUtil.getDayBetween(date, delivery_date);
        }
        return delivery;
    }

    public void setDelivery(int delivery) {
        this.delivery = delivery;
    }

    public OrderCommoditySupplier getOrderCommoditySupplier() {
        return orderCommoditySupplier;
    }

    public void setOrderCommoditySupplier(OrderCommoditySupplier orderCommoditySupplier) {
        this.orderCommoditySupplier = orderCommoditySupplier;
    }

    public CommodityActivity getSellLimit() {
        return sellLimit;
    }

    public void setSellLimit(CommodityActivity sellLimit) {
        this.sellLimit = sellLimit;
    }

    public InventoryOutCommodity getInventoryOutCommodity() {
        return inventoryOutCommodity;
    }

    public void setInventoryOutCommodity(InventoryOutCommodity inventoryOutCommodity) {
        this.inventoryOutCommodity = inventoryOutCommodity;
    }

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

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public CashBackCommodity getCashBackCommodity() {
        return cashBackCommodity;
    }

    public void setCashBackCommodity(CashBackCommodity cashBackCommodity) {
        this.cashBackCommodity = cashBackCommodity;
    }

    public OrderCommodityValueAddedCard getOrderCommodityValueAddedCard() {
        return orderCommodityValueAddedCard;
    }

    public void setOrderCommodityValueAddedCard(OrderCommodityValueAddedCard orderCommodityValueAddedCard) {
        this.orderCommodityValueAddedCard = orderCommodityValueAddedCard;
    }

    public ActivityCommodityExtend getActivityCommodity() {
        return activityCommodity;
    }

    public void setActivityCommodity(ActivityCommodityExtend activityCommodity) {
        this.activityCommodity = activityCommodity;
    }

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

    public int getArrivalNum() {
        return arrivalNum;
    }

    public void setArrivalNum(int arrivalNum) {
        this.arrivalNum = arrivalNum;
    }

    public CommodityStore getCommodityStore() {
        return commodityStore;
    }

    public void setCommodityStore(CommodityStore commodityStore) {
        this.commodityStore = commodityStore;
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
