package com.xinshan.utils.websocket.server;

import com.xinshan.utils.websocket.ByteUtil;
import com.xinshan.utils.websocket.WebSocketConstants;

/**
 * Created by mxt on 17-8-1.
 */
public class SyncServerUtil {
    private SyncServerUtil(){

    }
    private static SyncServerUtil syncServerUtil;
    public static SyncServerUtil getSyncServerUtil() {
        if (syncServerUtil == null) {
            syncServerUtil = new SyncServerUtil();
        }
        return syncServerUtil;
    }

    public void receiveMsg(byte[] data) {
        try {
            int cmd = ByteUtil.cmd(data);
            byte[] msg = ByteUtil.msg(data);
            System.out.println("onMessageByte\tcmd="+cmd + "\tmsg=" + new String(msg));
            receiveMsg(cmd, msg);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void receiveMsg(int cmd, byte[] msg) {
        int n = cmd/1000 * 1000;
        switch (n) {
            case WebSocketConstants.SYNC_HEART_BEAT:            break;
            case WebSocketConstants.SYNC_COMMODITY:             break;
            case WebSocketConstants.SYNC_SUPPLIER:              break;
            case WebSocketConstants.SYNC_ORDER:                 break;
            case WebSocketConstants.SYNC_PURCHASE:              break;
            case WebSocketConstants.SYNC_INVENTORY_IN:          break;
            case WebSocketConstants.SYNC_INVENTORY_OUT:         break;
            case WebSocketConstants.SYNC_INVENTORY_HISTORY:     break;
            case WebSocketConstants.SYNC_USER:                  break;
        }
    }
}
