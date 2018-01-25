package com.xinshan.utils.websocket.client;

import com.alibaba.fastjson.JSONObject;
import com.xinshan.components.user.UserShoppingComponent;
import com.xinshan.model.extend.user.UserShoppingExtend;
import com.xinshan.utils.websocket.WebSocketConstants;

/**
 * Created by mxt on 17-8-15.
 */
public class UserClientSyncHandler {
    private UserClientSyncHandler() {}
    private static UserClientSyncHandler userClientSyncHandler;

    public static UserClientSyncHandler getUserClientSyncHandler() {
        if (userClientSyncHandler == null) {
            userClientSyncHandler = new UserClientSyncHandler();
        }
        return userClientSyncHandler;
    }


    public void userSync(int cmd, byte[] msg) {
        switch (cmd) {
            case WebSocketConstants.SYNC_USER_CREATE_USER_SHOPPING:        createUserShopping(msg);     break;
            case WebSocketConstants.SYNC_USER_UPDATE_USER_SHOPPING:        updateUserShopping(msg);     break;
        }
    }

    /**
     * 添加来访记录
     * @param msg
     */
    private void createUserShopping(byte[] msg) {
        UserShoppingExtend userShopping = JSONObject.parseObject(msg, UserShoppingExtend.class);
        UserShoppingComponent.createUserShopping(userShopping);
    }

    /**
     * 编辑到访记录
     * @param msg
     */
    private void updateUserShopping(byte[] msg) {
        UserShoppingExtend userShopping = JSONObject.parseObject(msg, UserShoppingExtend.class);
        UserShoppingComponent.updateUserShopping(userShopping);
    }
}
