package com.xinshan.utils.websocket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Map;

/**
 *
 * Created by mxt on 17-7-27.
 */
public class MyWebSocketClient extends WebSocketClient {
    private String webSocketName;
    private String webSocketId;
    private Date createDate;

    public String getWebSocketName() {
        return webSocketName;
    }

    public void setWebSocketName(String webSocketName) {
        this.webSocketName = webSocketName;
    }

    public String getWebSocketId() {
        return webSocketId;
    }

    public void setWebSocketId(String webSocketId) {
        this.webSocketId = webSocketId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public MyWebSocketClient(URI serverURI) {
        super(serverURI);
        this.createDate = new Date();
    }

    public MyWebSocketClient(URI serverUri, Draft draft) {
        super(serverUri, draft);
        this.createDate = new Date();
    }

    public MyWebSocketClient(URI serverUri, Draft draft, Map<String, String> headers, int connecttimeout) {
        super(serverUri, draft, headers, connecttimeout);
        this.createDate = new Date();
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {

    }

    @Override
    public void onMessage(String s) {

    }

    @Override
    public void onClose(int i, String s, boolean b) {

    }

    @Override
    public void onError(Exception e) {

    }

    public void onMessage(ByteBuffer bytes) {

    }
}
