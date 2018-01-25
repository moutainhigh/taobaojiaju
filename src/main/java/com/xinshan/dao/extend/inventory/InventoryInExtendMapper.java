package com.xinshan.dao.extend.inventory;

import com.xinshan.model.*;
import com.xinshan.model.extend.inventory.InventoryHistoryDetailExtend;
import com.xinshan.model.extend.inventory.InventoryHistoryExtend;
import com.xinshan.model.extend.inventory.InventoryInCommodityExtend;
import com.xinshan.model.extend.inventory.InventoryInExtend;
import com.xinshan.pojo.inventory.InventorySearchOption;

import java.util.List;

/**
 * Created by mxt on 16-11-9.
 */
public interface InventoryInExtendMapper {
    void createInventoryIn(InventoryIn inventoryIn);
    void createInventoryInCommodity(InventoryInCommodity inventoryInCommodity);

    String maxInventoryInCode(String dateStr);

    List<InventoryInExtend> inventoryInList(InventorySearchOption inventorySearchOption);

    List<Integer> inventoryInIds(InventorySearchOption inventorySearchOption);
    Integer countInventoryIn(InventorySearchOption inventorySearchOption);

    InventoryInCommodity getInventoryInCommodityByPid(int purchase_commodity_id);
    InventoryInCommodity getInventoryInCommodityByAfterSalesCommodityId(int purchase_commodity_id);
    List<InventoryInCommodityExtend> inventoryInCommodities(InventorySearchOption inventorySearchOption);

    List<InventoryHistoryExtend> inventoryHistories(InventorySearchOption inventorySearchOption);

    List<InventoryHistoryDetailExtend> inventoryHistoryDetails(int inventory_in_history_id);

    Integer countInventoryHistory(InventorySearchOption inventorySearchOption);

    List<Integer> inventoryHistoryList(InventorySearchOption inventorySearchOption);
}
