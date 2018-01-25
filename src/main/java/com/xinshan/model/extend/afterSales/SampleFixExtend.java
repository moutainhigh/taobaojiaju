package com.xinshan.model.extend.afterSales;

import com.xinshan.model.CommoditySampleFix;
import com.xinshan.model.Supplier;
import com.xinshan.model.extend.orderFee.OrderFeeExtend;

import java.util.List;

/**
 * Created by mxt on 17-4-22.
 */
public class SampleFixExtend extends CommoditySampleFix{
    private List<SampleFixDetailExtend> sampleFixDetails;
    private List<OrderFeeExtend> orderFees;
    private Supplier supplier;

    public List<SampleFixDetailExtend> getSampleFixDetails() {
        return sampleFixDetails;
    }

    public void setSampleFixDetails(List<SampleFixDetailExtend> sampleFixDetails) {
        this.sampleFixDetails = sampleFixDetails;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public List<OrderFeeExtend> getOrderFees() {
        return orderFees;
    }

    public void setOrderFees(List<OrderFeeExtend> orderFees) {
        this.orderFees = orderFees;
    }
}
