package com.xinshan.pojo.afterSales;

import com.xinshan.pojo.SearchOption;

/**
 * Created by mxt on 17-4-22.
 */
public class SampleFixSearchOption extends SearchOption {
    private Integer sample_fix_id;
    private Integer supplier_id;

    public Integer getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(Integer supplier_id) {
        this.supplier_id = supplier_id;
    }

    public Integer getSample_fix_id() {
        return sample_fix_id;
    }

    public void setSample_fix_id(Integer sample_fix_id) {
        this.sample_fix_id = sample_fix_id;
    }
}
