package com.xinshan.dao.extend.commodity;

import com.xinshan.model.CommodityPriceAdjust;
import com.xinshan.model.CommodityPriceAdjustDetail;
import com.xinshan.model.extend.commodity.CommodityPriceAdjustDetailExtend;
import com.xinshan.model.extend.commodity.CommodityPriceAdjustExtend;
import com.xinshan.pojo.commodity.CommoditySearchOption;

import java.util.List;

/**
 * Created by mxt on 17-4-21.
 */
public interface CommodityPriceAdjustExtendMapper {

    void createCommodityPriceAdjust(CommodityPriceAdjust commodityPriceAdjust);

    void createAdjustDetail(CommodityPriceAdjustDetail commodityPriceAdjustDetail);

    List<CommodityPriceAdjustExtend> commodityPriceAdjustExtends(CommoditySearchOption commoditySearchOption);

    Integer countCommodityPriceAdjust(CommoditySearchOption commoditySearchOption);

    List<CommodityPriceAdjustDetailExtend> detailExtends(CommoditySearchOption commoditySearchOption);

    Integer countPriceAdjustDetail(CommoditySearchOption commoditySearchOption);

}
