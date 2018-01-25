package com.xinshan.utils.websocket.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.xinshan.utils.CommonUtils;
import com.xinshan.utils.DateUtil;
import com.xinshan.utils.EncryptionUtils;
import com.xinshan.utils.websocket.ByteUtil;
import com.xinshan.utils.websocket.MyWebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;

/**
 * Created by mxt on 17-7-28.
 */
public class SyncClient {

    private static MyWebSocketClient syncClient = null;

    public static MyWebSocketClient getSyncClient() {
        if (syncClient == null) {
            initSyncClient();
        }
        System.out.println("syncClientId:\t"+syncClient.getWebSocketId());
        return syncClient;
    }

    private static void initSyncClient() {
        try {
            syncClient = new MyWebSocketClient(new URI(CommonUtils.WEBSOCKET_URL + "/asdsafdffhgjyuhgfgfgcxvdsd"), new Draft_17()) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    System.out.println("syncClient open!");
                    setWebSocketName("syncClient");
                    setCreateDate(DateUtil.currentDate());
                    setWebSocketId(EncryptionUtils.encryption(DateUtil.currentTime() + "syncClient"));
                    WebSocketClientCollection.add(this);
                }

                @Override
                public void onClose(int i, String s, boolean b) {
                    WebSocketClientCollection.remove(this);
                    System.out.println("syncClient close!\t" + getWebSocketId());
                    syncClient = null;

                }

                @Override
                public void onError(Exception e) {

                }

                @Override
                public void onMessage(ByteBuffer bytes) {
                    System.out.println("syncClientId:\t"+getWebSocketId() + "\treceive msg");
                    SyncClientUtil.getSyncClientUtil().receiveMsg(bytes.array());
                }
            };
            syncClient.connect();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void send(int cmd, Object msg) {
        byte[] bytes = JSON.toJSONBytes(msg, SerializerFeature.DisableCircularReferenceDetect);
        byte[] data = new byte[bytes.length + 4];

        System.arraycopy(ByteUtil.intToBytes(cmd), 0, data, 0, 4);
        System.arraycopy(bytes, 0, data, 4, bytes.length);

        syncClient.send(data);
    }

}
