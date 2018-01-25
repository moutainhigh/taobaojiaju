package com.xinshan.components.websocket;

import com.xinshan.model.extend.commodity.CommodityExtend;
import com.xinshan.service.sync.CommoditySyncService;
import com.xinshan.utils.CommonUtils;
import com.xinshan.utils.websocket.WebSocketConstants;
import com.xinshan.utils.websocket.client.SyncClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by mxt on 17-7-28.
 */
@Component
public class CommoditySyncComponent {

    private static CommoditySyncService commoditySyncService;

    @Autowired
    public void setCommoditySyncService(CommoditySyncService commoditySyncService) {
        CommoditySyncComponent.commoditySyncService = commoditySyncService;
    }

    public static void commoditySync(CommodityExtend commodityExtend) {
        try {
            if (commodityExtend != null) {
                commoditySyncService.commoditySync(commodityExtend);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void commoditySync(int commodity_id) {
        if (!CommonUtils.WEBSOCKET_CLIENT) {
            return;
        }
        try {
            CommodityExtend commodityExtend = commoditySyncService.getCommodityById(commodity_id);
            SyncClient.send(WebSocketConstants.SYNC_COMMODITY, commodityExtend);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
