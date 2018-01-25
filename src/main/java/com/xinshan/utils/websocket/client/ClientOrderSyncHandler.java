package com.xinshan.utils.websocket.client;

import com.alibaba.fastjson.JSONObject;
import com.xinshan.components.order.OrderComponents;
import com.xinshan.model.UserShopping;
import com.xinshan.model.extend.user.UserShoppingExtend;
import com.xinshan.utils.websocket.WebSocketConstants;

/**
 * Created by mxt on 17-8-1.
 */
public class ClientOrderSyncHandler {
    private ClientOrderSyncHandler() {}
    private static ClientOrderSyncHandler clientSyncHandler;

    public static ClientOrderSyncHandler getClientSyncHandler() {
        if (clientSyncHandler == null) {
            clientSyncHandler = new ClientOrderSyncHandler();
        }
        return clientSyncHandler;
    }

    public void clientOrder(int cmd, byte[] msg) {
        switch (cmd) {
            case WebSocketConstants.SYNC_ORDER_SHOPPING:        shoppingOrder(msg);     break;
        }
    }

    /**
     * 手机订单
     * @param msg
     */
    private void shoppingOrder(byte[] msg) {
        UserShoppingExtend userShopping = JSONObject.parseObject(msg, UserShoppingExtend.class);
        OrderComponents.userShoppingCreateOrder(userShopping);
    }













}
