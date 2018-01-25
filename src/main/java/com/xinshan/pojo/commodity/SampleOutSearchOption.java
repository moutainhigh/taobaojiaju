package com.xinshan.pojo.commodity;

import com.xinshan.pojo.SearchOption;

import java.util.List;

/**
 * Created by mxt on 17-4-22.
 */
public class SampleOutSearchOption extends SearchOption {
    private Integer commodity_sample_out_id;
    private Integer commodity_id;
    private Integer supplier_id;
    private List<Integer> supplierIdList;

    public List<Integer> getSupplierIdList() {
        return supplierIdList;
    }

    public void setSupplierIdList(List<Integer> supplierIdList) {
        this.supplierIdList = supplierIdList;
    }

    public Integer getCommodity_sample_out_id() {
        return commodity_sample_out_id;
    }

    public void setCommodity_sample_out_id(Integer commodity_sample_out_id) {
        this.commodity_sample_out_id = commodity_sample_out_id;
    }

    public Integer getCommodity_id() {
        return commodity_id;
    }

    public void setCommodity_id(Integer commodity_id) {
        this.commodity_id = commodity_id;
    }

    public Integer getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(Integer supplier_id) {
        this.supplier_id = supplier_id;
    }
}
