package com.xinshan.utils.websocket.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.xinshan.model.extend.user.UserShoppingExtend;
import com.xinshan.utils.websocket.WebSocketConstants;

/**
 * Created by mxt on 17-8-15.
 */
public class UserServerSyncHandler {
    private UserServerSyncHandler() {}
    private static UserServerSyncHandler userServerSyncHandler;

    public static UserServerSyncHandler getUserServerSyncHandler() {
        if (userServerSyncHandler == null) {
            userServerSyncHandler = new UserServerSyncHandler();
        }
        return userServerSyncHandler;
    }

    /**
     * 添加进店记录
     * @param userShoppingExtend
     */
    public void sendCreateUserShopping(UserShoppingExtend userShoppingExtend) {
        byte[] bytes = JSON.toJSONBytes(userShoppingExtend, SerializerFeature.DisableCircularReferenceDetect);
        WebSocketConnections.send(WebSocketConstants.SYNC_USER_CREATE_USER_SHOPPING, bytes);
    }

    /**
     * 编辑进店记录
     * @param userShoppingExtend
     */
    public void sendUpdateUserShopping(UserShoppingExtend userShoppingExtend) {
        byte[] bytes = JSON.toJSONBytes(userShoppingExtend, SerializerFeature.DisableCircularReferenceDetect);
        WebSocketConnections.send(WebSocketConstants.SYNC_USER_UPDATE_USER_SHOPPING, bytes);
    }
}
