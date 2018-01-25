package com.xinshan.model.extend.inventory;

import com.xinshan.model.InventoryMove;

import java.util.List;

/**
 * Created by mxt on 17-2-7.
 */
public class InventoryMoveExtend extends InventoryMove {
    private List<InventoryMoveCommodityExtend> inventoryMoveCommodityExtends;

    public List<InventoryMoveCommodityExtend> getInventoryMoveCommodityExtends() {
        return inventoryMoveCommodityExtends;
    }

    public void setInventoryMoveCommodityExtends(List<InventoryMoveCommodityExtend> inventoryMoveCommodityExtends) {
        this.inventoryMoveCommodityExtends = inventoryMoveCommodityExtends;
    }
}
