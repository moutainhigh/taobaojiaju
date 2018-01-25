package com.xinshan.model.extend.commodity;

import com.xinshan.model.Commodity;
import com.xinshan.model.CommoditySampleIn;
import com.xinshan.model.CommoditySampleInDetail;

/**
 * Created by mxt on 17-5-15.
 */
public class SampleInDetailExtend extends CommoditySampleInDetail {
    private Commodity commodity;
    private CommoditySampleIn commoditySampleIn;

    public Commodity getCommodity() {
        return commodity;
    }

    public void setCommodity(Commodity commodity) {
        this.commodity = commodity;
    }

    public CommoditySampleIn getCommoditySampleIn() {
        return commoditySampleIn;
    }

    public void setCommoditySampleIn(CommoditySampleIn commoditySampleIn) {
        this.commoditySampleIn = commoditySampleIn;
    }
}
