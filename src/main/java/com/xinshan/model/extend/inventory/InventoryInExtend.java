package com.xinshan.model.extend.inventory;

import com.xinshan.model.*;

import java.util.List;

/**
 * Created by mxt on 16-11-10.
 */
public class InventoryInExtend extends InventoryIn{
    private Supplier supplier;
    private Order order;
    private Purchase purchase;
    private List<InventoryInCommodityExtend> inventoryInCommodities;
    private Employee printEmployee;
    private Gift gift;
    private User user;

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

    public Employee getPrintEmployee() {
        return printEmployee;
    }

    public void setPrintEmployee(Employee printEmployee) {
        this.printEmployee = printEmployee;
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

    public Purchase getPurchase() {
        return purchase;
    }

    public void setPurchase(Purchase purchase) {
        this.purchase = purchase;
    }

    public List<InventoryInCommodityExtend> getInventoryInCommodities() {
        return inventoryInCommodities;
    }

    public void setInventoryInCommodities(List<InventoryInCommodityExtend> inventoryInCommodities) {
        this.inventoryInCommodities = inventoryInCommodities;
    }
}
