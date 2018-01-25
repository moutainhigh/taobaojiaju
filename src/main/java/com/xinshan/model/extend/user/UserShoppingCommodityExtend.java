package com.xinshan.model.extend.user;

import com.xinshan.model.Commodity;
import com.xinshan.model.Supplier;
import com.xinshan.model.UserShoppingCommodity;

/**
 * Created by mxt on 17-7-31.
 */
public class UserShoppingCommodityExtend extends UserShoppingCommodity {
    private Commodity commodity;
    private Supplier supplier;

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
