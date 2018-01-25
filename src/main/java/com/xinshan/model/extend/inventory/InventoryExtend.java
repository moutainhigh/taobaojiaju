package com.xinshan.model.extend.inventory;

import com.xinshan.model.*;

/**
 * Created by mxt on 17-5-10.
 */
public class InventoryExtend extends Inventory {
    private CommodityNum commodityNum;
    private CommodityStore commodityStore;
    private Commodity commodity;
    private Supplier supplier;
    private CommodityUnit commodityUnit;
    private Category category;

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public CommodityUnit getCommodityUnit() {
        return commodityUnit;
    }

    public void setCommodityUnit(CommodityUnit commodityUnit) {
        this.commodityUnit = commodityUnit;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public CommodityNum getCommodityNum() {
        return commodityNum;
    }

    public void setCommodityNum(CommodityNum commodityNum) {
        this.commodityNum = commodityNum;
    }

    public CommodityStore getCommodityStore() {
        return commodityStore;
    }

    public void setCommodityStore(CommodityStore commodityStore) {
        this.commodityStore = commodityStore;
    }

    public Commodity getCommodity() {
        return commodity;
    }

    public void setCommodity(Commodity commodity) {
        this.commodity = commodity;
    }
}
