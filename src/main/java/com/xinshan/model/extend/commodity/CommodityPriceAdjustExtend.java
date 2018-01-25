package com.xinshan.model.extend.commodity;

import com.xinshan.model.CommodityPriceAdjust;

import java.util.List;

/**
 * Created by mxt on 17-4-21.
 */
public class CommodityPriceAdjustExtend extends CommodityPriceAdjust {

    private List<CommodityPriceAdjustDetailExtend> CommodityPriceAdjustDetails;

    public List<CommodityPriceAdjustDetailExtend> getCommodityPriceAdjustDetails() {
        return CommodityPriceAdjustDetails;
    }

    public void setCommodityPriceAdjustDetails(List<CommodityPriceAdjustDetailExtend> commodityPriceAdjustDetails) {
        CommodityPriceAdjustDetails = commodityPriceAdjustDetails;
    }
}
