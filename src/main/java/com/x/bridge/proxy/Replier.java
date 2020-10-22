package com.x.bridge.proxy;

import com.x.bridge.data.ChannelInfo;
import com.x.bridge.util.SocketHelper;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @Desc TODO
 * @Date 2020/10/22 19:03
 * @Author AD
 */
final class Replier {
    
    private final ChannelHandlerContext ctx;
    
    private final ChannelInfo channelInfo;
    
    private final AtomicLong recvSeq;
    
    private volatile boolean connected;
    
    private final Object connectLock;
    
    Replier(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        this.channelInfo = SocketHelper.getChannelInfo(ctx);
        this.recvSeq = new AtomicLong(-1);
        this.connected = false;
        this.connectLock = new Object();
    }
    
    void close() {
        ctx.close();
    }
    
    ChannelInfo getChannelInfo() {
        return channelInfo;
    }
    
    long getRecvSeq() {
        return recvSeq.get();
    }
    
    long recvSeqIncrement(){
        return recvSeq.incrementAndGet();
    }
    
    boolean isConnected() {
        return connected;
    }
    
    void setConnected(boolean connected) {
        this.connected = connected;
    }
    
    Object getConnectLock(){
        return connectLock;
    }
    
}
