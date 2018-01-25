package com.xinshan.model.extend.commodity;

import com.xinshan.model.CommoditySampleIn;
import com.xinshan.model.CommodityStore;
import com.xinshan.model.Supplier;

import java.util.List;

/**
 * Created by mxt on 17-5-15.
 */
public class SampleInExtend extends CommoditySampleIn {
    private Supplier supplier;
    private CommodityStore commodityStore;
    private List<SampleInDetailExtend> sampleInDetailExtends;

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public CommodityStore getCommodityStore() {
        return commodityStore;
    }

    public void setCommodityStore(CommodityStore commodityStore) {
        this.commodityStore = commodityStore;
    }

    public List<SampleInDetailExtend> getSampleInDetailExtends() {
        return sampleInDetailExtends;
    }

    public void setSampleInDetailExtends(List<SampleInDetailExtend> sampleInDetailExtends) {
        this.sampleInDetailExtends = sampleInDetailExtends;
    }
}
