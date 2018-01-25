package com.xinshan.model.extend.statistics;

import com.xinshan.model.SalesSupplier;
import com.xinshan.model.Supplier;

/**
 * Created by mxt on 17-9-5.
 */
public class SalesSupplierExtend extends SalesSupplier {
    private Supplier supplier;

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
}
