package com.xinshan.utils.websocket.client;

import com.xinshan.utils.websocket.ByteUtil;
import com.xinshan.utils.websocket.WebSocketConstants;

/**
 * Created by mxt on 17-7-31.
 */
public class SyncClientUtil {
    private SyncClientUtil(){

    }
    private static SyncClientUtil syncClientUtil;
    public static SyncClientUtil getSyncClientUtil() {
        if (syncClientUtil == null) {
            syncClientUtil = new SyncClientUtil();
        }
        return syncClientUtil;
    }


    public void receiveMsg(byte[] bytes) {
        try {
            int cmd = ByteUtil.cmd(bytes);
            byte[] msg = ByteUtil.msg(bytes);
            System.out.println("client receive msg\t" + cmd + "\t" + new String(msg));
            onMessage(cmd, msg);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onMessage(int cmd, byte[] msg) {
        int n = cmd/1000 * 1000;
        switch (n) {
            case WebSocketConstants.SYNC_HEART_BEAT:            break;
            case WebSocketConstants.SYNC_COMMODITY:             break;
            case WebSocketConstants.SYNC_SUPPLIER:              break;
            case WebSocketConstants.SYNC_ORDER:
                ClientOrderSyncHandler.getClientSyncHandler().clientOrder(cmd, msg);
                break;
            case WebSocketConstants.SYNC_PURCHASE:              break;
            case WebSocketConstants.SYNC_INVENTORY_IN:              break;
            case WebSocketConstants.SYNC_INVENTORY_OUT:         break;
            case WebSocketConstants.SYNC_INVENTORY_HISTORY:         break;
            case WebSocketConstants.SYNC_USER:
                UserClientSyncHandler.getUserClientSyncHandler().userSync(cmd, msg);
                break;
        }
    }


}
