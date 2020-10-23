package com.x.bridge.proxy;

import com.x.bridge.core.ISocketListener;
import com.x.bridge.core.SocketConfig;
import com.x.doraemon.util.Strings;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.log4j.Log4j2;

/**
 * @Desc TODO
 * @Date 2020/10/23 09:31
 * @Author AD
 */
@Log4j2
public class ProxyClientListener implements ISocketListener {
    
    @Override
    public void onStart(SocketConfig config) {
        log.info("客户端启动，IP={},Port={}", config.getIp(), config.getPort());
    }
    
    @Override
    public void onStartError(Throwable e) {
        log.error("客户端启动异常:{}", Strings.getExceptionTrace(e));
    }
    
    @Override
    public void onStop(SocketConfig config) {
        log.info("客户端关闭，IP={},Port={}", config.getIp(), config.getPort());
    }
    
    @Override
    public void active(ChannelHandlerContext ctx) throws Exception {
    
    }
    
    @Override
    public void inActive(ChannelHandlerContext ctx) throws Exception {
    
    }
    
    @Override
    public void receive(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
    
    }
    
    @Override
    public void timeout(ChannelHandlerContext ctx, IdleStateEvent event) throws Exception {
    
    }
    
    @Override
    public void error(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    
    }
    
}
