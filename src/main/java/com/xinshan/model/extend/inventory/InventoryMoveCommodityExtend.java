package com.xinshan.model.extend.inventory;

import com.xinshan.model.*;

/**
 * Created by mxt on 17-2-7.
 */
public class InventoryMoveCommodityExtend extends InventoryMoveCommodity {
    private CommodityNum commodityNum1;
    private CommodityNum commodityNum2;
    private CommodityStore commodityStore1;
    private CommodityStore commodityStore2;
    private Supplier supplier;
    private Integer sample;
    private Integer commodity_store_id;
    private Commodity commodity;

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public CommodityStore getCommodityStore1() {
        return commodityStore1;
    }

    public void setCommodityStore1(CommodityStore commodityStore1) {
        this.commodityStore1 = commodityStore1;
    }

    public CommodityStore getCommodityStore2() {
        return commodityStore2;
    }

    public void setCommodityStore2(CommodityStore commodityStore2) {
        this.commodityStore2 = commodityStore2;
    }

    public Integer getCommodity_store_id() {
        return commodity_store_id;
    }

    public void setCommodity_store_id(Integer commodity_store_id) {
        this.commodity_store_id = commodity_store_id;
    }

    public Integer getSample() {
        return sample;
    }

    public void setSample(Integer sample) {
        this.sample = sample;
    }

    public CommodityNum getCommodityNum1() {
        return commodityNum1;
    }

    public void setCommodityNum1(CommodityNum commodityNum1) {
        this.commodityNum1 = commodityNum1;
    }

    public CommodityNum getCommodityNum2() {
        return commodityNum2;
    }

    public void setCommodityNum2(CommodityNum commodityNum2) {
        this.commodityNum2 = commodityNum2;
    }

    public Commodity getCommodity() {
        return commodity;
    }

    public void setCommodity(Commodity commodity) {
        this.commodity = commodity;
    }
}
