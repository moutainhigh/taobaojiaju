package com.xinshan.dao.extend.inventory;

import com.xinshan.model.Inventory;
import com.xinshan.model.InventoryAdjust;
import com.xinshan.model.InventoryCarryOver;
import com.xinshan.model.extend.inventory.InventoryExtend;
import com.xinshan.pojo.inventory.InventoryCheckSearchOption;

import java.util.List;

/**
 * Created by mxt on 17-5-10.
 */
public interface InventoryCheckExtendMapper {

    void createInventory(Inventory inventory);

    void createInventoryAdjust(InventoryAdjust inventoryAdjust);

    void createInventoryCarryOver(InventoryCarryOver inventoryCarryOver);

    List<InventoryExtend> inventoryList(InventoryCheckSearchOption inventoryCheckSearchOption);

    Integer countInventory(InventoryCheckSearchOption inventoryCheckSearchOption);
}
