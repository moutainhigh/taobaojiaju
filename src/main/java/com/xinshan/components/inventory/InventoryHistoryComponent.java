package com.xinshan.components.inventory;

import com.xinshan.model.Logistics;
import com.xinshan.model.extend.inventory.InventoryHistoryExtend;
import com.xinshan.pojo.inventory.InventoryHistorySearchOption;
import com.xinshan.service.InventoryHistoryService;
import com.xinshan.utils.constant.inventory.InventoryHistoryConstant;
import com.xinshan.utils.constant.inventory.LogisticsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by mxt on 17-4-24.
 */
@Component
public class InventoryHistoryComponent {

    private static InventoryHistoryService inventoryHistoryService;

    @Autowired
    public void setInventoryHistoryService(InventoryHistoryService inventoryHistoryService) {
        InventoryHistoryComponent.inventoryHistoryService = inventoryHistoryService;
    }

    public static int orderInventoryOutStatus(int order_id) {
        InventoryHistorySearchOption inventoryHistorySearchOption = new InventoryHistorySearchOption();
        inventoryHistorySearchOption.setInventory_type(InventoryHistoryConstant.INVENTORY_HISTORY_TYPE_OUT + "");//订单出库
        inventoryHistorySearchOption.setOrder_id(order_id);
        List<InventoryHistoryExtend> inventoryHistoryExtends = inventoryHistoryService.inventoryHistories(inventoryHistorySearchOption);
        if (inventoryHistoryExtends == null || inventoryHistoryExtends.size() == 0) {
            return 0;
        }
        int status = status(inventoryHistoryExtends);
        return status;
    }

    /**
     * 0未送货
     * 1部分送货
     * 2送货完成
     * @param inventory_out_id
     * @return
     */
    public static int inventoryOutStatus(int inventory_out_id) {
        InventoryHistorySearchOption inventoryHistorySearchOption = new InventoryHistorySearchOption();
        inventoryHistorySearchOption.setInventory_type(InventoryHistoryConstant.INVENTORY_HISTORY_TYPE_OUT + "");//订单出库
        inventoryHistorySearchOption.setInventory_out_id(inventory_out_id);
        List<InventoryHistoryExtend> inventoryHistoryExtends = inventoryHistoryService.inventoryHistories(inventoryHistorySearchOption);
        if (inventoryHistoryExtends == null || inventoryHistoryExtends.size() == 0) {
            return 0;
        }
        int status = status(inventoryHistoryExtends);
        return status;
    }

    /**
     * 0未送货
     * 1部分送货
     * 2送货完成
     * @param inventoryHistoryExtends
     * @return
     */
    private static int status(List<InventoryHistoryExtend> inventoryHistoryExtends) {
        int status = 0;//2全部送到，1部分送到
        int done = 0;
        for (int i = 0; i < inventoryHistoryExtends.size(); i++) {
            InventoryHistoryExtend inventoryHistoryExtend = inventoryHistoryExtends.get(i);
            Logistics logistics = inventoryHistoryExtend.getLogistics();
            if (logistics != null && logistics.getLogistics_id() != null) {
                if (logistics.getLogistics_status() == LogisticsConstants.LOGISTICS_STATUS_DISTRIBUTION) {

                }else if (logistics.getLogistics_status() == LogisticsConstants.LOGISTICS_STATUS_DONE) {
                    done++;
                }
            }
        }
        if (done >= inventoryHistoryExtends.size()) {
            status = 2;
        }else if (done > 0) {
            status = 1;
        }
        return status;
    }

}
