package com.xinshan.dao.extend.commodity;

import com.xinshan.model.*;
import com.xinshan.model.extend.commodity.CommodityExtend;
import com.xinshan.pojo.commodity.CommoditySearchOption;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mxt on 16-10-17.
 */
public interface CommodityExtendMapper {

    void createColor(CommodityColor commodityColor);
    void createUnit(CommodityUnit commodityUnit);

    void createCommodity(Commodity commodity);
    List<CommodityUnit> units(CommoditySearchOption commoditySearchOption);
    List<CommodityColor> colors(CommoditySearchOption commoditySearchOption);

    List<CommodityExtend> commodityList(CommoditySearchOption commoditySearchOption);
    List<HashMap> commodityExport(CommoditySearchOption commoditySearchOption);
    Integer countCommodity(CommoditySearchOption commoditySearchOption);
    List<CommodityAttribute> commodityAttributes(int commodity);
    void deleteCommodityAttribute(int commodity);
    void createCommodityAttribute(CommodityAttribute commodityAttribute);

    List<CommodityExtend> commodityEasyList(CommoditySearchOption commoditySearchOption);

    void updateCommodityQrcode(Commodity commodity);

    CommodityExtend getCommodityByQRCode(String qrcode);

    void commodityGuangdong(int guangdong, int supplier_id);

    String maxSupplierCommodityCode();

    void updateCommodity(Commodity commodity);
}
