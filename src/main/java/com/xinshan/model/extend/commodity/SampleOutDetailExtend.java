package com.xinshan.model.extend.commodity;

import com.xinshan.model.Commodity;
import com.xinshan.model.CommodityNum;
import com.xinshan.model.CommoditySampleOutDetail;
import com.xinshan.model.CommodityStore;

/**
 * Created by mxt on 17-4-22.
 */
public class SampleOutDetailExtend extends CommoditySampleOutDetail {
    private Commodity commodity;
    private CommodityNum commodityNum;
    private CommodityStore commodityStore;

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
