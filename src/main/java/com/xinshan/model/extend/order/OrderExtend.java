package com.xinshan.model.extend.order;

import com.xinshan.model.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by mxt on 16-10-25.
 */
public class OrderExtend extends Order {
    private List<OrderCommodityExtend> orderCommodities;
    private List<OrderPay> orderPays;
    private BigDecimal pay_total_amount;//实际订单付款金额
    private Employee printEmployee;
    private List<OrderCommodityReturn> orderCommodityReturns;
    private List<OrderCarryFee> orderCarryFees;
    private Position position;
    private User user;
    private int gold_egg;
    private int gift;
    private String province_zip;
    private String city_zip;
    private String district_zip;

    public int getGift() {
        return gift;
    }

    public void setGift(int gift) {
        this.gift = gift;
    }

    public int getGold_egg() {
        return gold_egg;
    }

    public void setGold_egg(int gold_egg) {
        this.gold_egg = gold_egg;
    }

    public String getProvince_zip() {
        return province_zip;
    }

    public void setProvince_zip(String province_zip) {
        this.province_zip = province_zip;
    }

    public String getCity_zip() {
        return city_zip;
    }

    public void setCity_zip(String city_zip) {
        this.city_zip = city_zip;
    }

    public String getDistrict_zip() {
        return district_zip;
    }

    public void setDistrict_zip(String district_zip) {
        this.district_zip = district_zip;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public List<OrderCarryFee> getOrderCarryFees() {
        return orderCarryFees;
    }

    public void setOrderCarryFees(List<OrderCarryFee> orderCarryFees) {
        this.orderCarryFees = orderCarryFees;
    }

    public List<OrderCommodityReturn> getOrderCommodityReturns() {
        return orderCommodityReturns;
    }

    public void setOrderCommodityReturns(List<OrderCommodityReturn> orderCommodityReturns) {
        this.orderCommodityReturns = orderCommodityReturns;
    }

    public Employee getPrintEmployee() {
        return printEmployee;
    }

    public void setPrintEmployee(Employee printEmployee) {
        this.printEmployee = printEmployee;
    }

    public BigDecimal getPay_total_amount() {
        return pay_total_amount;
    }

    public void setPay_total_amount(BigDecimal pay_total_amount) {
        this.pay_total_amount = pay_total_amount;
    }

    public List<OrderPay> getOrderPays() {
        return orderPays;
    }

    public void setOrderPays(List<OrderPay> orderPays) {
        this.orderPays = orderPays;
    }

    public List<OrderCommodityExtend> getOrderCommodities() {
        return orderCommodities;
    }

    public void setOrderCommodities(List<OrderCommodityExtend> orderCommodities) {
        this.orderCommodities = orderCommodities;
    }
}
