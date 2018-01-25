package com.xinshan.model.extend.commodity;

import com.xinshan.model.*;

import java.util.List;

/**
 * Created by mxt on 16-10-18.
 */
public class CommodityExtend extends Commodity {
    private Category category;
    private CommodityColor commodityColor;
    private CommodityUnit commodityUnit;
    private List<CommodityAttribute> commodityAttributes;
    private Supplier supplier;
    private SupplierSeries supplierSeries;
    private CommodityActivity commodityActivity;
    private List<Activity> activities;//商品参与活动

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    public CommodityActivity getCommodityActivity() {
        return commodityActivity;
    }

    public void setCommodityActivity(CommodityActivity commodityActivity) {
        this.commodityActivity = commodityActivity;
    }

    public SupplierSeries getSupplierSeries() {
        return supplierSeries;
    }

    public void setSupplierSeries(SupplierSeries supplierSeries) {
        this.supplierSeries = supplierSeries;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public List<CommodityAttribute> getCommodityAttributes() {
        return commodityAttributes;
    }

    public void setCommodityAttributes(List<CommodityAttribute> commodityAttributes) {
        this.commodityAttributes = commodityAttributes;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public CommodityColor getCommodityColor() {
        return commodityColor;
    }

    public void setCommodityColor(CommodityColor commodityColor) {
        this.commodityColor = commodityColor;
    }

    public CommodityUnit getCommodityUnit() {
        return commodityUnit;
    }

    public void setCommodityUnit(CommodityUnit commodityUnit) {
        this.commodityUnit = commodityUnit;
    }

    /*public CommodityStore getCommodityStore() {
        return commodityStore;
    }

    public void setCommodityStore(CommodityStore commodityStore) {
        this.commodityStore = commodityStore;
    }

    public CommodityNum getCommodityNum() {
        return commodityNum;
    }

    public void setCommodityNum(CommodityNum commodityNum) {
        this.commodityNum = commodityNum;
    }*/
}
