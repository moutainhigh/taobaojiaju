package com.xinshan.dao.extend.inventory;

import com.xinshan.model.InventoryOut;
import com.xinshan.model.InventoryOutCommodity;
import com.xinshan.model.Logistics;
import com.xinshan.model.extend.inventory.InventoryOutCommodityExtend;
import com.xinshan.model.extend.inventory.InventoryOutExtend;
import com.xinshan.pojo.inventory.InventorySearchOption;

import java.util.List;

/**
 * Created by mxt on 16-11-11.
 */
public interface InventoryOutExtendMapper {
    List<InventoryOutExtend> inventoryOutList(InventorySearchOption inventorySearchOption);

    List<Integer> inventoryOutIds(InventorySearchOption inventorySearchOption);

    Integer countInventoryOut(InventorySearchOption inventorySearchOption);

    void createInventoryOut(InventoryOut inventoryOut);

    void createInventoryOutCommodity(InventoryOutCommodity inventoryOutCommodity);

    String inventoryOutCode(String dateStr);

    List<InventoryOutCommodityExtend> inventoryOutCommodities(InventorySearchOption inventorySearchOption);

    void createLogistics(Logistics logistics);

    InventoryOutCommodity getInventoryOutCommodityByOrderCommodityId(int order_commodity_id);
}
