package com.xinshan.pojo.order;

import com.xinshan.pojo.SearchOption;
import com.xinshan.utils.DateUtil;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by mxt on 16-10-25.
 */
public class OrderSearchOption extends SearchOption {
    private List<Integer> orderIds;
    private Integer order_status;
    private String orderStatuses;
    private Integer order_id;
    private Date orderStartDate;//订单开始时间
    private Date orderEndDate;//订单结束时间
    private String customer_phone_number;
    private Integer supplier_id;
    private String cashier_employee_code;
    private String guide_employee_code;
    private String order_commodity_status;
    private String employee_code;
    private List<Integer> orderReturnIds;
    private Integer order_return_id;
    private Integer after_sales_id;
    private Integer after_sales_commodity_id;
    private Integer order_commodity_id;
    private List<Integer> commodityIds;
    private List<Integer> supplierIds;
    private BigDecimal total_price;
    private String order_step;
    private Integer order_return_pay_status;
    private Integer order_return_type;
    private Integer order_return_commodity_type;
    private String user_phone;
    private Integer employee_position;
    private Integer user_id;
    private String order_return_check_status;
    private List<Integer> orderCommodityIdList;
    private Integer order_return_commodity_id;
    private String inventory_out_commodity_status;
    private Integer order_commodity_return_status;
    private Integer trans_purchase;
    private String contacts;
    private Integer order_commodity_type;
    private Integer gold_egg;
    private Integer commodity_num;
    private Integer order_commodity_supplier_status;

    public Integer getCommodity_num() {
        return commodity_num;
    }

    public void setCommodity_num(Integer commodity_num) {
        this.commodity_num = commodity_num;
    }

    public Integer getOrder_commodity_supplier_status() {
        return order_commodity_supplier_status;
    }

    public void setOrder_commodity_supplier_status(Integer order_commodity_supplier_status) {
        this.order_commodity_supplier_status = order_commodity_supplier_status;
    }

    public Integer getGold_egg() {
        return gold_egg;
    }

    public void setGold_egg(Integer gold_egg) {
        this.gold_egg = gold_egg;
    }

    public Integer getOrder_commodity_type() {
        return order_commodity_type;
    }

    public void setOrder_commodity_type(Integer order_commodity_type) {
        this.order_commodity_type = order_commodity_type;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public Integer getTrans_purchase() {
        return trans_purchase;
    }

    public void setTrans_purchase(Integer trans_purchase) {
        this.trans_purchase = trans_purchase;
    }

    public Integer getOrder_commodity_return_status() {
        return order_commodity_return_status;
    }

    public void setOrder_commodity_return_status(Integer order_commodity_return_status) {
        this.order_commodity_return_status = order_commodity_return_status;
    }

    public String getInventory_out_commodity_status() {
        return inventory_out_commodity_status;
    }

    public void setInventory_out_commodity_status(String inventory_out_commodity_status) {
        this.inventory_out_commodity_status = inventory_out_commodity_status;
    }

    public Integer getOrder_return_commodity_id() {
        return order_return_commodity_id;
    }

    public void setOrder_return_commodity_id(Integer order_return_commodity_id) {
        this.order_return_commodity_id = order_return_commodity_id;
    }

    public List<Integer> getOrderCommodityIdList() {
        return orderCommodityIdList;
    }

    public void setOrderCommodityIdList(List<Integer> orderCommodityIdList) {
        this.orderCommodityIdList = orderCommodityIdList;
    }

    public String getOrder_return_check_status() {
        return order_return_check_status;
    }

    public void setOrder_return_check_status(String order_return_check_status) {
        this.order_return_check_status = order_return_check_status;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getEmployee_position() {
        return employee_position;
    }

    public void setEmployee_position(Integer employee_position) {
        this.employee_position = employee_position;
    }

    public List<Integer> getSupplierIds() {
        return supplierIds;
    }

    public void setSupplierIds(List<Integer> supplierIds) {
        this.supplierIds = supplierIds;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public Integer getOrder_return_commodity_type() {
        return order_return_commodity_type;
    }

    public void setOrder_return_commodity_type(Integer order_return_commodity_type) {
        this.order_return_commodity_type = order_return_commodity_type;
    }

    public Integer getOrder_return_type() {
        return order_return_type;
    }

    public void setOrder_return_type(Integer order_return_type) {
        this.order_return_type = order_return_type;
    }

    public Integer getOrder_return_pay_status() {
        return order_return_pay_status;
    }

    public void setOrder_return_pay_status(Integer order_return_pay_status) {
        this.order_return_pay_status = order_return_pay_status;
    }

    public String getOrder_step() {
        return order_step;
    }

    public void setOrder_step(String order_step) {
        this.order_step = order_step;
    }

    public BigDecimal getTotal_price() {
        return total_price;
    }

    public void setTotal_price(BigDecimal total_price) {
        this.total_price = total_price;
    }

    public List<Integer> getCommodityIds() {
        return commodityIds;
    }

    public void setCommodityIds(List<Integer> commodityIds) {
        this.commodityIds = commodityIds;
    }

    public Integer getOrder_commodity_id() {
        return order_commodity_id;
    }

    public void setOrder_commodity_id(Integer order_commodity_id) {
        this.order_commodity_id = order_commodity_id;
    }

    public Integer getAfter_sales_commodity_id() {
        return after_sales_commodity_id;
    }

    public void setAfter_sales_commodity_id(Integer after_sales_commodity_id) {
        this.after_sales_commodity_id = after_sales_commodity_id;
    }

    public Integer getAfter_sales_id() {
        return after_sales_id;
    }

    public void setAfter_sales_id(Integer after_sales_id) {
        this.after_sales_id = after_sales_id;
    }

    public Integer getOrder_return_id() {
        return order_return_id;
    }

    public void setOrder_return_id(Integer order_return_id) {
        this.order_return_id = order_return_id;
    }

    public List<Integer> getOrderReturnIds() {
        return orderReturnIds;
    }

    public void setOrderReturnIds(List<Integer> orderReturnIds) {
        this.orderReturnIds = orderReturnIds;
    }

    public String getEmployee_code() {
        return employee_code;
    }

    public void setEmployee_code(String employee_code) {
        this.employee_code = employee_code;
    }

    public String getOrder_commodity_status() {
        return order_commodity_status;
    }

    public void setOrder_commodity_status(String order_commodity_status) {
        this.order_commodity_status = order_commodity_status;
    }

    public String getCashier_employee_code() {
        return cashier_employee_code;
    }

    public void setCashier_employee_code(String cashier_employee_code) {
        this.cashier_employee_code = cashier_employee_code;
    }

    public String getGuide_employee_code() {
        return guide_employee_code;
    }

    public void setGuide_employee_code(String guide_employee_code) {
        this.guide_employee_code = guide_employee_code;
    }

    public Integer getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(Integer supplier_id) {
        this.supplier_id = supplier_id;
    }

    public String getCustomer_phone_number() {
        return customer_phone_number;
    }

    public void setCustomer_phone_number(String customer_phone_number) {
        this.customer_phone_number = customer_phone_number;
    }

    public Date getOrderEndDate() {
        return orderEndDate;
    }

    public void setOrderEndDate(Date orderEndDate) {
        if (orderEndDate != null) {
            orderEndDate = DateUtil.endOfTheDay(orderEndDate);
        }
        this.orderEndDate = orderEndDate;
    }

    public Date getOrderStartDate() {
        return orderStartDate;
    }

    public void setOrderStartDate(Date orderStartDate) {
        this.orderStartDate = orderStartDate;
    }

    public Integer getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
    }

    public Integer getOrder_status() {
        return order_status;
    }

    public void setOrder_status(Integer order_status) {
        this.order_status = order_status;
    }

    public String getOrderStatuses() {
        return orderStatuses;
    }

    public void setOrderStatuses(String orderStatuses) {
        this.orderStatuses = orderStatuses;
    }

    public List<Integer> getOrderIds() {
        return orderIds;
    }

    public void setOrderIds(List<Integer> orderIds) {
        this.orderIds = orderIds;
    }
}
