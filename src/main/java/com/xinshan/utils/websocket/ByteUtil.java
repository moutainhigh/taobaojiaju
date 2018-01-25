package com.xinshan.utils.websocket;

/**
 * Created by mxt on 17-8-1.
 */
public class ByteUtil {

    public static int getCmd(byte[] b) {
        int cmd = 0;
        if (b.length < 4) {
            return cmd;
        }
        cmd = (int) ( ((b[0] & 0xFF)<<24)|((b[0+1] & 0xFF)<<16)|((b[0+2] & 0xFF)<<8)|(b[0+3] & 0xFF));
        return cmd;
    }

    public static byte[] intToBytes(int cmd) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte)((cmd >>> 24) & 0xFF);
        bytes[1] = (byte)((cmd >>> 16) & 0xFF);
        bytes[2] = (byte)((cmd >>>  8) & 0xFF);
        bytes[3] = (byte)((cmd >>>  0) & 0xFF);
        return bytes;
    }

    public static int cmd(byte[] data) {
        return getCmd(data);
    }

    public static byte[] msg(byte[] data) {
        byte[] msg = new byte[data.length - 4];
        System.arraycopy(data, 4, msg, 0, msg.length);
        return msg;
    }
}
