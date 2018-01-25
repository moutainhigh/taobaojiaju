package com.xinshan.model.extend.activity;

import com.xinshan.model.*;
import com.xinshan.model.extend.commodity.CommodityExtend;
import com.xinshan.model.extend.supplier.SupplierExtend;

import java.util.List;

/**
 * Created by mxt on 17-2-10.
 */
public class ActivityExtend extends Activity{

    private ActivityDetail activityDetail;//特价活动
    private List<ActivityCommodityExtend> activityCommodities;

    private ActivityValueAdded activityValueAdd;//增值活动

    private ActivityCashBack activityCashBack;//返现活动信息
    private List<CommodityExtend> activityCashBackCommodities;//
    private List<SupplierExtend> cashBackSuppliers;

    private ActivityGoldEgg activityGoldEgg;//金蛋活动

    private List<SupplierExtend> supplierExtends;

    private ActivityBrand activityBrand;//品牌助推活动
    private ActivitySpecialRight activitySpecialRight;//特价权活动

    private List<SpecialRightCommodityExtend> specialRightCommodities;//

    private List<SellLimitExtend> sellLimits;

    public List<SellLimitExtend> getSellLimits() {
        return sellLimits;
    }

    public void setSellLimits(List<SellLimitExtend> sellLimits) {
        this.sellLimits = sellLimits;
    }

    public List<SupplierExtend> getCashBackSuppliers() {
        return cashBackSuppliers;
    }

    public void setCashBackSuppliers(List<SupplierExtend> cashBackSuppliers) {
        this.cashBackSuppliers = cashBackSuppliers;
    }

    public ActivitySpecialRight getActivitySpecialRight() {
        return activitySpecialRight;
    }

    public void setActivitySpecialRight(ActivitySpecialRight activitySpecialRight) {
        this.activitySpecialRight = activitySpecialRight;
    }

    public List<SpecialRightCommodityExtend> getSpecialRightCommodities() {
        return specialRightCommodities;
    }

    public void setSpecialRightCommodities(List<SpecialRightCommodityExtend> specialRightCommodities) {
        this.specialRightCommodities = specialRightCommodities;
    }

    public ActivityBrand getActivityBrand() {
        return activityBrand;
    }

    public void setActivityBrand(ActivityBrand activityBrand) {
        this.activityBrand = activityBrand;
    }

    public List<SupplierExtend> getSupplierExtends() {
        return supplierExtends;
    }

    public void setSupplierExtends(List<SupplierExtend> supplierExtends) {
        this.supplierExtends = supplierExtends;
    }

    public ActivityGoldEgg getActivityGoldEgg() {
        return activityGoldEgg;
    }

    public void setActivityGoldEgg(ActivityGoldEgg activityGoldEgg) {
        this.activityGoldEgg = activityGoldEgg;
    }

    public List<CommodityExtend> getActivityCashBackCommodities() {
        return activityCashBackCommodities;
    }

    public void setActivityCashBackCommodities(List<CommodityExtend> activityCashBackCommodities) {
        this.activityCashBackCommodities = activityCashBackCommodities;
    }

    public ActivityCashBack getActivityCashBack() {
        return activityCashBack;
    }

    public void setActivityCashBack(ActivityCashBack activityCashBack) {
        this.activityCashBack = activityCashBack;
    }

    public ActivityValueAdded getActivityValueAdd() {
        return activityValueAdd;
    }

    public void setActivityValueAdd(ActivityValueAdded activityValueAdd) {
        this.activityValueAdd = activityValueAdd;
    }

    public ActivityDetail getActivityDetail() {
        return activityDetail;
    }

    public void setActivityDetail(ActivityDetail activityDetail) {
        this.activityDetail = activityDetail;
    }

    public List<ActivityCommodityExtend> getActivityCommodities() {
        return activityCommodities;
    }

    public void setActivityCommodities(List<ActivityCommodityExtend> activityCommodities) {
        this.activityCommodities = activityCommodities;
    }
}
