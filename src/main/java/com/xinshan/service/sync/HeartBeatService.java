package com.xinshan.service.sync;

import com.xinshan.utils.CommonUtils;
import com.xinshan.utils.DateUtil;
import com.xinshan.utils.websocket.client.WebSocketClientCollection;
import com.xinshan.utils.websocket.client.SyncClient;
import org.springframework.stereotype.Service;

/**
 * Created by mxt on 17-7-28.
 */
@Service
public class HeartBeatService {

    public void run() {
        //System.out.println(DateUtil.format(DateUtil.currentDate(), "yyyy-MM-dd HH:mm:ss") + "---------------------------------HeartBeatService");
        if (CommonUtils.WEBSOCKET_CLIENT) {
            SyncClient.getSyncClient();
            WebSocketClientCollection.heartBeat();
        }
    }
}
