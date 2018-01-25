package com.xinshan.model.extend.statistics;

import java.math.BigDecimal;

/**
 * Created by mxt on 17-3-13.
 */
public class SaleRank {
    private Integer category_id;
    private String category_name;
    private Integer supplier_id;
    private String supplier_name;
    private Integer position_id;
    private String position_name;
    private String employee_code;
    private String employee_name;
    private Integer commodity_id;
    private String commodity_name;
    private Integer commodity_num;
    private BigDecimal commodity_total_price;

    public Integer getCategory_id() {
        return category_id;
    }

    public void setCategory_id(Integer category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public Integer getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(Integer supplier_id) {
        this.supplier_id = supplier_id;
    }

    public String getSupplier_name() {
        return supplier_name;
    }

    public void setSupplier_name(String supplier_name) {
        this.supplier_name = supplier_name;
    }

    public Integer getPosition_id() {
        return position_id;
    }

    public void setPosition_id(Integer position_id) {
        this.position_id = position_id;
    }

    public String getPosition_name() {
        return position_name;
    }

    public void setPosition_name(String position_name) {
        this.position_name = position_name;
    }

    public String getEmployee_code() {
        return employee_code;
    }

    public void setEmployee_code(String employee_code) {
        this.employee_code = employee_code;
    }

    public String getEmployee_name() {
        return employee_name;
    }

    public void setEmployee_name(String employee_name) {
        this.employee_name = employee_name;
    }

    public Integer getCommodity_id() {
        return commodity_id;
    }

    public void setCommodity_id(Integer commodity_id) {
        this.commodity_id = commodity_id;
    }

    public String getCommodity_name() {
        return commodity_name;
    }

    public void setCommodity_name(String commodity_name) {
        this.commodity_name = commodity_name;
    }

    public Integer getCommodity_num() {
        return commodity_num;
    }

    public void setCommodity_num(Integer commodity_num) {
        this.commodity_num = commodity_num;
    }

    public BigDecimal getCommodity_total_price() {
        return commodity_total_price;
    }

    public void setCommodity_total_price(BigDecimal commodity_total_price) {
        this.commodity_total_price = commodity_total_price;
    }
}
