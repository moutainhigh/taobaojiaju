package com.xinshan.utils.websocket.server;

import com.xinshan.utils.websocket.ByteUtil;
import com.xinshan.utils.websocket.server.SyncWebSocket;

import javax.websocket.Session;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by mxt on 17-7-31.
 */
public class WebSocketConnections {
    private static Lock lock = new ReentrantLock();
    private static List<SyncWebSocket> connections = new ArrayList<>();

    public static List<SyncWebSocket> getConnections() {
        return connections;
    }

    public static void add(SyncWebSocket syncWebSocket) {
        lock.lock();
        try {
            System.out.println("add\t"+syncWebSocket.getSession().getId());
            connections.add(syncWebSocket);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public static void remove(SyncWebSocket syncWebSocket) {
        lock.lock();
        try {
            System.out.println("remove\t"+syncWebSocket.getSession().getId());
            connections.remove(syncWebSocket);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public static void send(int cmd, byte[] msg) {
        try {
            byte[] bytes = ByteUtil.intToBytes(cmd);
            byte[] data = new byte[4 + msg.length];
            System.arraycopy(bytes, 0, data, 0, 4);
            System.arraycopy(msg, 0, data, 4, msg.length);
            List<SyncWebSocket> connections = getConnections();
            Iterator<SyncWebSocket> iterator = connections.iterator();
            while (iterator.hasNext()) {
                SyncWebSocket syncWebSocket = iterator.next();
                Session session = syncWebSocket.getSession();
                if (session != null && session.isOpen()) {
                    try {
                        System.out.println("webSocketId:\t" + syncWebSocket.getSession().getId() +"server send\t" + cmd + "\t" + new String(msg));
                        session.getBasicRemote().sendBinary(ByteBuffer.wrap(data));
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    remove(syncWebSocket);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
