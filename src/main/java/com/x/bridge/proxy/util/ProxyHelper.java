package com.x.bridge.proxy.util;

import com.x.bridge.proxy.data.ChannelInfo;
import com.x.doraemon.util.ArrayHelper;
import com.x.doraemon.util.Strings;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Desc
 * @Date 2020/10/22 01:29
 * @Author AD
 */
public final class ProxyHelper {
    
    public static ChannelInfo getChannelInfo(ChannelHandlerContext ctx) {
        String remoteAddress = ctx.channel().remoteAddress().toString().substring(1);
        String localAddress = ctx.channel().localAddress().toString().substring(1);
        return new ChannelInfo(remoteAddress, localAddress);
    }

    public static String getIP(String appAddress) {
        if (Strings.isNotNull(appAddress)) {
            return appAddress.split(":")[0];
        }
        return appAddress;
    }

    public static int getPort(String appAddress) {
        if (Strings.isNotNull(appAddress)) {
            return Integer.parseInt(appAddress.split(":")[1]);
        }
        return -1;
    }
    
    public static byte[] readData(ByteBuf buf) {
        if (buf != null) {
            int len = buf.readableBytes();
            byte[] data = new byte[len];
            buf.readBytes(data);
            return data;
        }
        return ArrayHelper.EMPTY_BYTE;
    }
    
}
