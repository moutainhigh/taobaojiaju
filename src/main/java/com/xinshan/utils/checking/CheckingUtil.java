package com.xinshan.utils.checking;

import com.xinshan.model.InventoryHistoryDetail;

/**
 * Created by mxt on 17-2-27.
 */
public class CheckingUtil {
    private CheckingUtil() {
    }
    private static CheckingUtil checkingUtil;
    public static CheckingUtil getCheckingUtil() {
        if (checkingUtil == null) {
            checkingUtil = new CheckingUtil();
        }
        return checkingUtil;
    }

    public boolean supplierOut(InventoryHistoryDetail inventoryHistoryDetail) {
        boolean supplier_out = false;
        if (inventoryHistoryDetail.getSupplier_out() == null) {
            if (inventoryHistoryDetail.getInventory_in_out() == 1 && inventoryHistoryDetail.getInventory_history_num() == 0 && inventoryHistoryDetail.getCommodity_num_id() == 0) {
                supplier_out = true;
            }
        }else if (inventoryHistoryDetail.getSupplier_out().equals(1)) {
            supplier_out = true;
        }
        return supplier_out;
    }

}
