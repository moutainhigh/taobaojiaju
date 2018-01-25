package com.xinshan.utils.websocket.server;

import com.xinshan.utils.CommonUtils;
import com.xinshan.utils.websocket.ByteUtil;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by mxt on 17-7-28.
 */
@ServerEndpoint("/ws/{token}")
public class SyncWebSocket {
    private Session session;

    public Session getSession() {
        return session;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam(value = "token") String token) {
        System.out.println("socket connect\t" + session.getId());
        if (!CommonUtils.WEBSOCKET_SERVER) {
            try {
                session.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        this.session = session;
        WebSocketConnections.add(this);
    }

    @OnMessage
    public void onMessage(byte[] data) {
        try {
            int cmd = ByteUtil.getCmd(data);
            byte[] msg = new byte[data.length - 4];
            System.arraycopy(data, 4, msg, 0, msg.length);
            System.out.println("server receive msg\t sessionId:" + session.getId() +"\t" + cmd + "\t" + new String(msg));
            ByteBuffer wrap = ByteBuffer.wrap(data);
            session.getBasicRemote().sendBinary(wrap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(String s) {
        System.out.println("onMessageStr:"+s);
    }

    @OnClose
    public void onClose() {
        System.out.println("socket close\t" + session.getId());
        WebSocketConnections.remove(this);
        session = null;
    }

}
