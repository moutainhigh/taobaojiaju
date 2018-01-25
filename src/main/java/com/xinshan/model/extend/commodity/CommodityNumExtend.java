package com.xinshan.model.extend.commodity;

import com.xinshan.model.*;

import java.util.List;

/**
 * Created by mxt on 16-11-9.
 */
public class CommodityNumExtend extends CommodityNum {
    private Commodity commodity;
    private CommodityStore commodityStore;
    private Supplier supplier;
    private CommodityColor commodityColor;
    private CommodityUnit commodityUnit;
    private Category category;
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

    public Commodity getCommodity() {
        return commodity;
    }

    public void setCommodity(Commodity commodity) {
        this.commodity = commodity;
    }

    public CommodityStore getCommodityStore() {
        return commodityStore;
    }

    public void setCommodityStore(CommodityStore commodityStore) {
        this.commodityStore = commodityStore;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
