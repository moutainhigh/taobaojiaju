package com.xinshan.model.extend.supplier;

import com.xinshan.model.Supplier;
import com.xinshan.model.SupplierSeries;

/**
 * Created by mxt on 16-10-31.
 */
public class SupplierSeriesExtend extends SupplierSeries {
    private Supplier supplier;

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
}
