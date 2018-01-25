package com.xinshan.pojo.commodity;

import com.xinshan.pojo.SearchOption;

import java.util.List;

/**
 * Created by mxt on 17-5-15.
 */
public class SampleInSearchOption extends SearchOption {
    private List<Integer> sampleInIds;
    private Integer supplier_id;
    private Integer commodity_id;
    private Integer commodity_store_id;
    private Integer commodity_sample_in_id;
    private Integer sample_in_confirm_status;
    private List<Integer> supplierIdList;

    public List<Integer> getSupplierIdList() {
        return supplierIdList;
    }

    public void setSupplierIdList(List<Integer> supplierIdList) {
        this.supplierIdList = supplierIdList;
    }

    public Integer getSample_in_confirm_status() {
        return sample_in_confirm_status;
    }

    public void setSample_in_confirm_status(Integer sample_in_confirm_status) {
        this.sample_in_confirm_status = sample_in_confirm_status;
    }

    public Integer getCommodity_sample_in_id() {
        return commodity_sample_in_id;
    }

    public void setCommodity_sample_in_id(Integer commodity_sample_in_id) {
        this.commodity_sample_in_id = commodity_sample_in_id;
    }

    public List<Integer> getSampleInIds() {
        return sampleInIds;
    }

    public void setSampleInIds(List<Integer> sampleInIds) {
        this.sampleInIds = sampleInIds;
    }

    public Integer getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(Integer supplier_id) {
        this.supplier_id = supplier_id;
    }

    public Integer getCommodity_id() {
        return commodity_id;
    }

    public void setCommodity_id(Integer commodity_id) {
        this.commodity_id = commodity_id;
    }

    public Integer getCommodity_store_id() {
        return commodity_store_id;
    }

    public void setCommodity_store_id(Integer commodity_store_id) {
        this.commodity_store_id = commodity_store_id;
    }
}
