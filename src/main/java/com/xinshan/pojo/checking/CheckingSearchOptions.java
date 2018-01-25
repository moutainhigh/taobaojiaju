package com.xinshan.pojo.checking;

import com.xinshan.pojo.SearchOption;

/**
 * Created by mxt on 17-2-21.
 */
public class CheckingSearchOptions extends SearchOption {
    private String checking_code;
    private String order_code;
    private Integer supplier_id;
    private Integer checking_id;
    private Integer checking_type;
    private Integer payment_status;
    private String contacts;

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public Integer getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(Integer payment_status) {
        this.payment_status = payment_status;
    }

    public Integer getChecking_type() {
        return checking_type;
    }

    public void setChecking_type(Integer checking_type) {
        this.checking_type = checking_type;
    }

    public Integer getChecking_id() {
        return checking_id;
    }

    public void setChecking_id(Integer checking_id) {
        this.checking_id = checking_id;
    }

    public Integer getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(Integer supplier_id) {
        this.supplier_id = supplier_id;
    }

    public String getChecking_code() {
        return checking_code;
    }

    public void setChecking_code(String checking_code) {
        this.checking_code = checking_code;
    }

    public String getOrder_code() {
        return order_code;
    }

    public void setOrder_code(String order_code) {
        this.order_code = order_code;
    }
}
