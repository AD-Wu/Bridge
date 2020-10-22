package com.x.bridge.util;

import io.netty.channel.ChannelHandlerContext;

/**
 * @Desc TODO
 * @Date 2020/10/22 01:29
 * @Author AD
 */
public final class SocketHelper {
    
    public static String getRemoteAddress(ChannelHandlerContext ctx){
        return ctx.channel().remoteAddress().toString().substring(1);
    }
    
    public static String getLocalAddress(ChannelHandlerContext ctx){
        return ctx.channel().localAddress().toString().substring(1);
    }
    
    public static String getRemoteIP(ChannelHandlerContext ctx){
        return getRemoteAddress(ctx).split(":")[0];
    }
    
    public static String getLocalIP(ChannelHandlerContext ctx){
        return getLocalAddress(ctx).split(":")[0];
    }
    
    public static int getRemotePort(ChannelHandlerContext ctx){
        return Integer.parseInt(getRemoteAddress(ctx).split(":")[1]);
    }
    
    public static int getLocalPort(ChannelHandlerContext ctx){
        return Integer.parseInt(getLocalAddress(ctx).split(":")[1]);
    }
}
