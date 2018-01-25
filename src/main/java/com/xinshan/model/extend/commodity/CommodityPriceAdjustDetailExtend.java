package com.xinshan.model.extend.commodity;

import com.xinshan.model.Commodity;
import com.xinshan.model.CommodityPriceAdjust;
import com.xinshan.model.CommodityPriceAdjustDetail;

/**
 * Created by mxt on 17-4-21.
 */
public class CommodityPriceAdjustDetailExtend extends CommodityPriceAdjustDetail {
    private Commodity commodity;
    private CommodityPriceAdjust commodityPriceAdjust;

    public Commodity getCommodity() {
        return commodity;
    }

    public void setCommodity(Commodity commodity) {
        this.commodity = commodity;
    }

    public CommodityPriceAdjust getCommodityPriceAdjust() {
        return commodityPriceAdjust;
    }

    public void setCommodityPriceAdjust(CommodityPriceAdjust commodityPriceAdjust) {
        this.commodityPriceAdjust = commodityPriceAdjust;
    }
}
