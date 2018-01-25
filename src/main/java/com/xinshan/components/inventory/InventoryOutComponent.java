package com.xinshan.components.inventory;

import com.xinshan.model.InventoryOutCommodity;
import com.xinshan.model.extend.inventory.InventoryOutExtend;
import com.xinshan.pojo.inventory.InventorySearchOption;
import com.xinshan.service.InventoryOutService;
import com.xinshan.utils.constant.inventory.InventoryConstant;
import com.xinshan.utils.constant.order.OrderConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by mxt on 17-4-24.
 */
@Component
public class InventoryOutComponent {

    private static InventoryOutService inventoryOutService;

    @Autowired
    public void setInventoryOutService(InventoryOutService inventoryOutService) {
        InventoryOutComponent.inventoryOutService = inventoryOutService;
    }

    /**
     * 出库状态
     * 0未出库
     * 1部分出库
     * 2全部出库
     * @param order_id
     * @return
     */
    public static Integer inventoryOutStatus(int order_id) {
        InventorySearchOption inventorySearchOption = new InventorySearchOption();
        inventorySearchOption.setOrder_id(order_id);
        inventorySearchOption.setInventory_out_type(InventoryConstant.INVENTORY_OUT_TYPE_ORDER);
        List<Integer> inventoryOutIds = inventoryOutService.inventoryOutIds(inventorySearchOption);
        if (inventoryOutIds == null || inventoryOutIds.size() <= 0) {
            return 0;
        }
        inventorySearchOption.setInventoryOutIds(inventoryOutIds);
        List<InventoryOutExtend> inventoryOutExtends = inventoryOutService.inventoryOutList(inventorySearchOption);
        int inventoryOutStatus = 0;
        for (int i = 0; i < inventoryOutExtends.size(); i++) {
            InventoryOutExtend inventoryOutExtend = inventoryOutExtends.get(i);
            Integer inventory_out_status = inventoryOutExtend.getInventory_out_status();
            if (inventory_out_status == null) {
                inventory_out_status = 0;
            }
            if (inventory_out_status == 0) {
                inventoryOutStatus = 0; break;
            }else if (inventory_out_status == 1) {
                inventoryOutStatus = 1; break;
            }else if (inventory_out_status == 2) {
                inventoryOutStatus = 2;
            }
        }
        return inventoryOutStatus;
    }

    /**
     *
     * @param order_commodity_id
     * @return
     * 0未出库，1部分出库，2完全出库
     */
    public static int orderCommodityOutStatus(int order_commodity_id) {
        InventoryOutCommodity inventoryOutCommodity = inventoryOutService.getInventoryOutCommodityByOrderCommodityId(order_commodity_id);
        if (inventoryOutCommodity != null) {
            Integer inventory_out_commodity_status = inventoryOutCommodity.getInventory_out_commodity_status();
            //0未出库，1部分出库，2完全出库
            if (inventory_out_commodity_status == 0) {
                return 0;
            }else if (inventory_out_commodity_status == 1) {
                int i = InventoryHistoryComponent.inventoryOutStatus(inventoryOutCommodity.getInventory_out_id());
                if (i == 1 || i == 2) {//1部分送货
                    return OrderConstants.ORDER_COMMODITY_STATUS_PART_OUT;
                } else if (i == 2){
                    return OrderConstants.ORDER_COMMODITY_STATUS_PART_DONE;
                } else {
                    return OrderConstants.ORDER_COMMODITY_STATUS_PART_OUT;
                }
            }else if (inventory_out_commodity_status == 2) {//全部出库
                int i = InventoryHistoryComponent.inventoryOutStatus(inventoryOutCommodity.getInventory_out_id());
                if (i == 1) {//1部分送货
                    return OrderConstants.ORDER_COMMODITY_STATUS_COMPLETE_OUT;
                }else if(i == 2){//全部到货
                    return OrderConstants.ORDER_COMMODITY_STATUS_ALL_DONE;
                } else {
                    return OrderConstants.ORDER_COMMODITY_STATUS_COMPLETE_OUT;
                }
            }
            return 0;
        }else {
            return 0;//未确认
        }
    }
}
