package com.xinshan.dao.extend.inventory;

import com.xinshan.model.InventoryMove;
import com.xinshan.model.InventoryMoveCommodity;
import com.xinshan.model.extend.inventory.InventoryMoveExtend;
import com.xinshan.pojo.inventory.InventorySearchOption;

import java.util.List;

/**
 * Created by mxt on 17-2-7.
 */
public interface InventoryMoveExtendMapper {

    void createInventoryMove(InventoryMove inventoryMove);

    void createInventoryMoveCommodity(InventoryMoveCommodity inventoryMoveCommodity);

    InventoryMoveExtend inventoryMove(InventorySearchOption inventorySearchOption);

    List<Integer> inventoryMoveIds(InventorySearchOption inventorySearchOption);

    Integer countInventoryMove(InventorySearchOption inventorySearchOption);

    List<InventoryMoveExtend> inventoryMove1(InventorySearchOption inventorySearchOption);

}
