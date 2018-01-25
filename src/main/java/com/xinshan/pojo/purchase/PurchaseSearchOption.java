package com.xinshan.pojo.purchase;

import com.xinshan.pojo.SearchOption;

import java.util.List;

/**
 * Created by mxt on 16-11-2.
 */
public class PurchaseSearchOption extends SearchOption {
    private Integer purchase_id;
    private Integer commodity_id;
    private String purchase_code;
    private List<Integer> purchaseIds;
    private Integer supplier_id;
    private Integer order_id;
    private String order_code;
    private Integer purchase_commodity_status;
    private String employee_code;
    private String statuses;
    private Integer purchase_type;
    private Integer return_commodity;

    public Integer getReturn_commodity() {
        return return_commodity;
    }

    public void setReturn_commodity(Integer return_commodity) {
        this.return_commodity = return_commodity;
    }

    public Integer getPurchase_type() {
        return purchase_type;
    }

    public void setPurchase_type(Integer purchase_type) {
        this.purchase_type = purchase_type;
    }

    public String getStatuses() {
        return statuses;
    }

    public void setStatuses(String statuses) {
        this.statuses = statuses;
    }

    public String getEmployee_code() {
        return employee_code;
    }

    public void setEmployee_code(String employee_code) {
        this.employee_code = employee_code;
    }

    public Integer getPurchase_commodity_status() {
        return purchase_commodity_status;
    }

    public void setPurchase_commodity_status(Integer purchase_commodity_status) {
        this.purchase_commodity_status = purchase_commodity_status;
    }

    public String getOrder_code() {
        return order_code;
    }

    public void setOrder_code(String order_code) {
        this.order_code = order_code;
    }

    public Integer getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
    }

    public Integer getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(Integer supplier_id) {
        this.supplier_id = supplier_id;
    }

    public List<Integer> getPurchaseIds() {
        return purchaseIds;
    }

    public void setPurchaseIds(List<Integer> purchaseIds) {
        this.purchaseIds = purchaseIds;
    }

    public Integer getPurchase_id() {
        return purchase_id;
    }

    public void setPurchase_id(Integer purchase_id) {
        this.purchase_id = purchase_id;
    }

    public Integer getCommodity_id() {
        return commodity_id;
    }

    public void setCommodity_id(Integer commodity_id) {
        this.commodity_id = commodity_id;
    }

    public String getPurchase_code() {
        return purchase_code;
    }

    public void setPurchase_code(String purchase_code) {
        this.purchase_code = purchase_code;
    }
}
