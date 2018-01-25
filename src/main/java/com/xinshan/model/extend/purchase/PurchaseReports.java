package com.xinshan.model.extend.purchase;

import com.xinshan.model.*;

import java.util.List;

/**
 * Created by mxt on 17-2-4.
 */
public class PurchaseReports extends PurchaseCommodity {
    private Purchase purchase;
    private Order order;
    private OrderCommodity orderCommodity;
    private Commodity commodity;
    private Supplier supplier;
    private List<PurchaseInCommodity> purchaseInCommodities;

    public List<PurchaseInCommodity> getPurchaseInCommodities() {
        return purchaseInCommodities;
    }

    public void setPurchaseInCommodities(List<PurchaseInCommodity> purchaseInCommodities) {
        this.purchaseInCommodities = purchaseInCommodities;
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

    public Purchase getPurchase() {
        return purchase;
    }

    public void setPurchase(Purchase purchase) {
        this.purchase = purchase;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public OrderCommodity getOrderCommodity() {
        return orderCommodity;
    }

    public void setOrderCommodity(OrderCommodity orderCommodity) {
        this.orderCommodity = orderCommodity;
    }
}
