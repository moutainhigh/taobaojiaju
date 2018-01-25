package com.xinshan.pojo.payment;

import com.xinshan.pojo.SearchOption;

/**
 * Created by mxt on 17-3-8.
 */
public class PaymentSearchOption extends SearchOption {
    private Integer checking_id;
    private Integer supplier_id;

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
}
