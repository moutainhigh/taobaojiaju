package com.xinshan.model.extend.activity;

import com.xinshan.model.ActivitySpecialRightCommodity;
import com.xinshan.model.Commodity;
import com.xinshan.model.Supplier;

/**
 * Created by mxt on 17-4-20.
 */
public class SpecialRightCommodityExtend extends ActivitySpecialRightCommodity {
    private Supplier supplier;
    private Commodity commodity;

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Commodity getCommodity() {
        return commodity;
    }

    public void setCommodity(Commodity commodity) {
        this.commodity = commodity;
    }
}
