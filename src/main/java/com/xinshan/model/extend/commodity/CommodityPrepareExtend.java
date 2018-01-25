package com.xinshan.model.extend.commodity;

import com.xinshan.model.*;

import java.util.List;

/**
 * Created by mxt on 17-9-6.
 */
public class CommodityPrepareExtend extends CommodityPrepare {
    private Category category;
    private CommodityColor commodityColor;
    private CommodityUnit commodityUnit;
    private List<CommodityAttribute> commodityAttributes;
    private Supplier supplier;
    private SupplierSeries supplierSeries;

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

    public List<CommodityAttribute> getCommodityAttributes() {
        return commodityAttributes;
    }

    public void setCommodityAttributes(List<CommodityAttribute> commodityAttributes) {
        this.commodityAttributes = commodityAttributes;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public SupplierSeries getSupplierSeries() {
        return supplierSeries;
    }

    public void setSupplierSeries(SupplierSeries supplierSeries) {
        this.supplierSeries = supplierSeries;
    }
}
