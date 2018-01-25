package com.xinshan.model.extend.checking;

import com.xinshan.model.Checking;
import com.xinshan.model.Payment;
import com.xinshan.model.Supplier;
import com.xinshan.model.extend.settlement.SettlementExtend;

import java.util.List;

/**
 * Created by mxt on 17-2-21.
 */
public class CheckingExtend extends Checking {
    private List<CheckingDetailExtend> checkingDetails;
    private Supplier supplier;
    private List<Payment> payments;
    private SettlementExtend settlementExtend;

    public SettlementExtend getSettlementExtend() {
        return settlementExtend;
    }

    public void setSettlementExtend(SettlementExtend settlementExtend) {
        this.settlementExtend = settlementExtend;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    public List<CheckingDetailExtend> getCheckingDetails() {
        return checkingDetails;
    }

    public void setCheckingDetails(List<CheckingDetailExtend> checkingDetails) {
        this.checkingDetails = checkingDetails;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
}
