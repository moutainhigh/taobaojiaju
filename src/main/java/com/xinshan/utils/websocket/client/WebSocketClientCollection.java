package com.xinshan.utils.websocket.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.xinshan.utils.DateUtil;
import com.xinshan.utils.websocket.MyWebSocketClient;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * Created by mxt on 17-7-27.
 */
public class WebSocketClientCollection {

    private static Lock lock = new ReentrantLock();
    private static List<MyWebSocketClient> webSocketClients = new ArrayList<>();

    public static List<MyWebSocketClient> getWebSocketClients() {
        return webSocketClients;
    }

    public static void add(MyWebSocketClient webSocketClient) {
        lock.lock();
        try {
            System.out.println("client add\t"+webSocketClient.getWebSocketId());
            webSocketClients.add(webSocketClient);
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public static void remove(MyWebSocketClient webSocketClient) {
        lock.lock();
        try {
            System.out.println("client remove:\t"+webSocketClient.getWebSocketId());
            webSocketClients.remove(webSocketClient);
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public static void remove(int index) {
        lock.lock();
        try {
            MyWebSocketClient myWebSocketClient = webSocketClients.get(index);
            System.out.println("remove:"+myWebSocketClient.getWebSocketName());
            webSocketClients.remove(index);
        }catch (Exception e) {

        }finally {
            lock.unlock();
        }
    }

    /**
     * 心跳
     */
    public static void heartBeat() {
        List<MyWebSocketClient> webSocketClients = getWebSocketClients();
        for (int i = 0; i < webSocketClients.size(); i++) {
            MyWebSocketClient webSocketClient = webSocketClients.get(i);
            if (webSocketClient == null) {
                remove(i);
                continue;
            }
            try {
                if (webSocketClient.getConnection().isOpen()) {
                    Date date = DateUtil.currentDate();
                    String format = DateUtil.format(date, "yyyy-MM-dd HH:mm:ss.SSS") + "\t" + webSocketClient.getWebSocketId();
                    byte[] cmd = {0, 0, 3, -24};
                    byte[] bytes = format.getBytes();
                    byte[] data = new byte[bytes.length + 4];
                    System.arraycopy(cmd, 0, data, 0, 4);
                    System.arraycopy(bytes, 0, data, 4, bytes.length);
                    webSocketClient.send(data);
                    System.out.println(webSocketClient.getWebSocketId() + "" + "\theart beat:\t" + format);
                }
            }catch (Exception e) {
                e.printStackTrace();
                remove(i);
            }
        }
    }
}
