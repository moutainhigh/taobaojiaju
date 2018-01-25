package com.xinshan.pojo.statistics;

import com.xinshan.pojo.SearchOption;

import java.math.BigDecimal;

/**
 * Created by mxt on 17-3-13.
 */
public class StatisticsSearchOption extends SearchOption {
    private Integer supplier_id;
    private String employee_code;
    private Integer position_id;
    private String month;
    private String contacts;
    private BigDecimal sales_amount;
    private Integer m;//月
    private Integer y;//年

    public Integer getM() {
        return m;
    }

    public void setM(Integer m) {
        this.m = m;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public BigDecimal getSales_amount() {
        return sales_amount;
    }

    public void setSales_amount(BigDecimal sales_amount) {
        this.sales_amount = sales_amount;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public Integer getPosition_id() {
        return position_id;
    }

    public void setPosition_id(Integer position_id) {
        this.position_id = position_id;
    }

    public String getEmployee_code() {
        return employee_code;
    }

    public void setEmployee_code(String employee_code) {
        this.employee_code = employee_code;
    }

    public Integer getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(Integer supplier_id) {
        this.supplier_id = supplier_id;
    }
}
