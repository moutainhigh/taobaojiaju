package com.xinshan.model.extend.inventory;

import com.xinshan.model.*;

import java.util.List;

/**
 * Created by mxt on 16-11-11.
 */
public class InventoryOutExtend extends InventoryOut{
    private List<InventoryOutCommodityExtend> inventoryOutCommodities;
    private Order order;
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

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<InventoryOutCommodityExtend> getInventoryOutCommodities() {
        return inventoryOutCommodities;
    }

    public void setInventoryOutCommodities(List<InventoryOutCommodityExtend> inventoryOutCommodities) {
        this.inventoryOutCommodities = inventoryOutCommodities;
    }
}
