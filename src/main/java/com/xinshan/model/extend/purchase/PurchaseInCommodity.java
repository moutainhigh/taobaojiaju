package com.xinshan.model.extend.purchase;

import java.util.Date;

/**
 * Created by mxt on 17-3-10.
 */
public class PurchaseInCommodity {
    private String inventory_in_code;
    private Date confirm_in_date;
    private Integer purchase_period;

    public String getInventory_in_code() {
        return inventory_in_code;
    }

    public void setInventory_in_code(String inventory_in_code) {
        this.inventory_in_code = inventory_in_code;
    }

    public Date getConfirm_in_date() {
        return confirm_in_date;
    }

    public void setConfirm_in_date(Date confirm_in_date) {
        this.confirm_in_date = confirm_in_date;
    }

    public Integer getPurchase_period() {
        return purchase_period;
    }

    public void setPurchase_period(Integer purchase_period) {
        this.purchase_period = purchase_period;
    }
}
