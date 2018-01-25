package com.xinshan.dao.extend.inventory;

import com.xinshan.model.CommodityNum;
import com.xinshan.model.CommodityStore;
import com.xinshan.model.extend.commodity.CommodityNumExtend;
import com.xinshan.model.extend.inventory.InventoryHistoryDetailExtend;
import com.xinshan.pojo.commodity.CommoditySearchOption;
import com.xinshan.pojo.inventory.InventorySearchOption;

import java.util.List;
import java.util.Map;

/**
 * Created by mxt on 16-11-9.
 */
public interface InventoryExtendMapper {

    CommodityNum getNumByCommodityIdAndStoreId(CommoditySearchOption commoditySearchOption);

    List<CommodityNumExtend> commodityNumList(CommoditySearchOption commoditySearchOption);

    Integer countCommodityNum(CommoditySearchOption commoditySearchOption);

    Integer InventoryNum(CommoditySearchOption commoditySearchOption);

    void createCommodityNum(CommodityNum commodityNum);

    List<Map> inventoryHistoryStatics(InventorySearchOption inventorySearchOption);

    List<CommodityStore> stores(CommoditySearchOption commoditySearchOption);
    void createStore(CommodityStore commodityStore);
}
