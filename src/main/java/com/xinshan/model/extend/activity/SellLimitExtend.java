package com.xinshan.model.extend.activity;

import com.xinshan.model.Activity;
import com.xinshan.model.ActivitySellLimit;
import com.xinshan.model.Commodity;
import com.xinshan.model.Supplier;

/**
 * Created by mxt on 17-9-18.
 */
public class SellLimitExtend extends ActivitySellLimit {
    private Activity activity;
    private Supplier supplier;
    private Commodity commodity;
    private int order_num;
    private int unconfirmed_order_num;
    private int return_num;
    private int return_add_num;

    public int getUnconfirmed_order_num() {
        return unconfirmed_order_num;
    }

    public void setUnconfirmed_order_num(int unconfirmed_order_num) {
        this.unconfirmed_order_num = unconfirmed_order_num;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public int getOrder_num() {
        return order_num;
    }

    public void setOrder_num(int order_num) {
        this.order_num = order_num;
    }

    public int getReturn_num() {
        return return_num;
    }

    public void setReturn_num(int return_num) {
        this.return_num = return_num;
    }

    public int getReturn_add_num() {
        return return_add_num;
    }

    public void setReturn_add_num(int return_add_num) {
        this.return_add_num = return_add_num;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Commodity getCommodity() {
        return commodity;
    }

    public void setCommodity(Commodity commodity) {
        this.commodity = commodity;
    }
}
