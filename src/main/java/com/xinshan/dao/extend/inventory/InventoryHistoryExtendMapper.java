package com.xinshan.dao.extend.inventory;

import com.xinshan.model.InventoryHistory;
import com.xinshan.model.InventoryHistoryDetail;
import com.xinshan.model.extend.inventory.InventoryHistoryDetailExtend;
import com.xinshan.model.extend.inventory.InventoryHistoryExtend;
import com.xinshan.pojo.inventory.InventoryHistorySearchOption;
import com.xinshan.pojo.inventory.InventorySearchOption;

import java.util.List;
import java.util.Map;

/**
 * Created by mxt on 17-4-24.
 */
public interface InventoryHistoryExtendMapper {

    List<InventoryHistoryExtend> inventoryHistories(InventoryHistorySearchOption inventoryHistorySearchOption);

    List<Integer> inventoryHistoryIds(InventoryHistorySearchOption inventoryHistorySearchOption);

    Integer countInventoryHistory(InventoryHistorySearchOption inventoryHistorySearchOption);

    List<InventoryHistoryDetailExtend> inventoryHistoryDetails(int inventory_history_id);

    void createInventoryHistory(InventoryHistory inventoryHistory);

    void createInventoryHistoryDetail(InventoryHistoryDetail inventoryHistoryDetail);

    String todayInventoryHistoryCode(String str);

    List<InventoryHistoryDetailExtend> inventoryHistoryDetailExtends(InventorySearchOption inventorySearchOption);

    Integer countInventoryHistoryDetailExtends(InventorySearchOption inventorySearchOption);

    List<Map> historyReportExport(InventorySearchOption inventorySearchOption);
}
