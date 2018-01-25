package com.xinshan.model.extend.purchase;

import com.xinshan.model.*;

import java.util.List;

/**
 * Created by mxt on 16-11-2.
 */
public class PurchaseExtend extends Purchase {
    private List<PurchaseCommodityExtend> purchaseCommodities;
    private Supplier supplier;
    private Order order;
    private Employee printEmployee;
    private User user;
    private Gift gift;

    public Gift getGift() {
        return gift;
    }

    public void setGift(Gift gift) {
        this.gift = gift;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Employee getPrintEmployee() {
        return printEmployee;
    }

    public void setPrintEmployee(Employee printEmployee) {
        this.printEmployee = printEmployee;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public List<PurchaseCommodityExtend> getPurchaseCommodities() {
        return purchaseCommodities;
    }

    public void setPurchaseCommodities(List<PurchaseCommodityExtend> purchaseCommodities) {
        this.purchaseCommodities = purchaseCommodities;
    }
}
