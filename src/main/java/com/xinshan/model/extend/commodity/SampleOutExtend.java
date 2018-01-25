package com.xinshan.model.extend.commodity;

import com.xinshan.model.CommoditySampleOut;
import com.xinshan.model.Supplier;

import java.util.List;

/**
 * Created by mxt on 17-4-22.
 */
public class SampleOutExtend extends CommoditySampleOut {
    private List<SampleOutDetailExtend> sampleOutDetails;
    private Supplier supplier;

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public List<SampleOutDetailExtend> getSampleOutDetails() {
        return sampleOutDetails;
    }

    public void setSampleOutDetails(List<SampleOutDetailExtend> sampleOutDetails) {
        this.sampleOutDetails = sampleOutDetails;
    }
}
