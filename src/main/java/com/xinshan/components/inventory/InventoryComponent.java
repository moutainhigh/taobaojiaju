package com.xinshan.components.inventory;

import com.xinshan.model.extend.commodity.CommodityNumExtend;
import com.xinshan.pojo.commodity.CommoditySearchOption;
import com.xinshan.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * Created by mxt on 17-6-2.
 */
@Component
public class InventoryComponent {

    private static InventoryService inventoryService;

    @Autowired
    public void setInventoryService(InventoryService inventoryService) {
        InventoryComponent.inventoryService = inventoryService;
    }

    public static List<CommodityNumExtend> commodityExport(CommoditySearchOption commoditySearchOption) throws IOException {
        return inventoryService.commodityNumList(commoditySearchOption);
    }
}
