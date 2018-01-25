package com.xinshan.utils.websocket.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.xinshan.model.UserShopping;
import com.xinshan.model.extend.user.UserShoppingExtend;
import com.xinshan.utils.websocket.WebSocketConstants;

/**
 * Created by mxt on 17-8-1.
 *
 */
public class ServerOrderSyncHandler {
    private ServerOrderSyncHandler() {

    }
    private static ServerOrderSyncHandler serverOrderSyncHandler;

    public static ServerOrderSyncHandler getServerSyncHandler() {
        if (serverOrderSyncHandler == null) {
            serverOrderSyncHandler = new ServerOrderSyncHandler();
        }
        return serverOrderSyncHandler;
    }

    /**
     * 进店记录下订单
     * @param userShoppingExtend
     */
    public void sendUserShoppingCreateOrder(UserShoppingExtend userShoppingExtend) {
        byte[] bytes = JSON.toJSONBytes(userShoppingExtend, SerializerFeature.DisableCircularReferenceDetect);
        WebSocketConnections.send(WebSocketConstants.SYNC_ORDER_SHOPPING, bytes);
    }


}
