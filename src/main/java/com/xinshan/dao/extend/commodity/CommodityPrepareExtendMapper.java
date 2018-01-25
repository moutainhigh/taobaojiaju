package com.xinshan.dao.extend.commodity;

import com.xinshan.model.CommodityPrepare;
import com.xinshan.model.extend.commodity.CommodityPrepareExtend;
import com.xinshan.pojo.commodity.CommoditySearchOption;

import java.util.List;

/**
 * Created by mxt on 17-9-6.
 */
public interface CommodityPrepareExtendMapper {

    void createCommodityPrepare(CommodityPrepare commodityPrepare);

    List<CommodityPrepareExtend> commodityPrepareList(CommoditySearchOption commoditySearchOption);

    Integer countCommodityPrepare(CommoditySearchOption commoditySearchOption);
}
