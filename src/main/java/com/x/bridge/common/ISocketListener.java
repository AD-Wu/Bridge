package com.x.bridge.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @Desc
 * @Date 2020/10/21 19:40
 * @Author AD
 */
public interface ISocketListener {
    
    void active(ChannelHandlerContext ctx) throws Exception;
    
    void inActive(ChannelHandlerContext ctx) throws Exception;
    
    void receive(ChannelHandlerContext ctx, ByteBuf buf) throws Exception;
    
    void timeout(ChannelHandlerContext ctx, IdleStateEvent event) throws Exception;
    
    void error(ChannelHandlerContext ctx, Throwable cause) throws Exception;
    
}
