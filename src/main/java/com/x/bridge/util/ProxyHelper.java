package com.x.bridge.util;

import com.x.bridge.data.ChannelInfo;
import com.x.doraemon.util.ArrayHelper;
import com.x.doraemon.util.StringHelper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetAddress;
import java.net.UnknownHostException;

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
        if (StringHelper.isNotNull(appAddress)) {
            if (appAddress.contains(":")) {
                return appAddress.split(":")[0];
            } else {
                try {
                    return InetAddress.getByName(appAddress).getHostAddress();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    return appAddress;
                }
            }
            
        }
        return appAddress;
    }
    
    public static int getPort(String appAddress) {
        if (StringHelper.isNotNull(appAddress)) {
            if (appAddress.contains(":")) {
                return Integer.parseInt(appAddress.split(":")[1]);
            }
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
    
    public static void main(String[] args) throws Exception {
        ByteBuf buf = Unpooled.buffer();
        buf.writeBytes(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20});
        String hex = ByteBufUtil.hexDump(buf);
        System.out.println(hex);
    }
    
    
}
