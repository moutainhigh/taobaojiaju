package com.xinshan.model.extend.inventory;

import com.xinshan.model.*;
import com.xinshan.model.extend.gift.GiftExtend;

import java.util.List;

/**
 * Created by mxt on 16-12-2.
 */
public class InventoryHistoryExtend extends InventoryHistory {
    private List<InventoryHistoryDetailExtend> inventoryHistoryDetails;
    private Order order;
    private Employee employee;
    private Logistics logistics;
    private List<OrderCarryFee> orderCarryFees;
    private GiftExtend giftExtend;

    public GiftExtend getGiftExtend() {
        return giftExtend;
    }

    public void setGiftExtend(GiftExtend giftExtend) {
        this.giftExtend = giftExtend;
    }

    public List<OrderCarryFee> getOrderCarryFees() {
        return orderCarryFees;
    }

    public void setOrderCarryFees(List<OrderCarryFee> orderCarryFees) {
        this.orderCarryFees = orderCarryFees;
    }

    public Logistics getLogistics() {
        return logistics;
    }

    public void setLogistics(Logistics logistics) {
        this.logistics = logistics;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<InventoryHistoryDetailExtend> getInventoryHistoryDetails() {
        return inventoryHistoryDetails;
    }

    public void setInventoryHistoryDetails(List<InventoryHistoryDetailExtend> inventoryHistoryDetails) {
        this.inventoryHistoryDetails = inventoryHistoryDetails;
    }
}
