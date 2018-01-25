package com.xinshan.model.extend.payment;

import com.xinshan.model.Checking;
import com.xinshan.model.Payment;
import com.xinshan.model.Supplier;

/**
 * Created by mxt on 17-3-8.
 */
public class PaymentExtend extends Payment {
    private Checking checking;
    private Supplier supplier;

    public Checking getChecking() {
        return checking;
    }

    public void setChecking(Checking checking) {
        this.checking = checking;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
}
