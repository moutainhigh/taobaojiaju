package com.xinshan.model.extend.afterSales;

import com.xinshan.model.Commodity;
import com.xinshan.model.CommoditySampleFixDetail;

/**
 * Created by mxt on 17-4-22.
 */
public class SampleFixDetailExtend extends CommoditySampleFixDetail {
    private Commodity commodity;

    public Commodity getCommodity() {
        return commodity;
    }

    public void setCommodity(Commodity commodity) {
        this.commodity = commodity;
    }
}
