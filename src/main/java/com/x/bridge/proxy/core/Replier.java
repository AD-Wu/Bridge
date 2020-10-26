package com.x.bridge.proxy.core;

import com.x.bridge.proxy.data.ChannelInfo;
import com.x.bridge.proxy.util.ProxyHelper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @Desc 通道应答对象
 * @Date 2020/10/22 19:03
 * @Author AD
 */
@Data
@Log4j2
public final class Replier {
    
    private final String appSocketClient;
    
    private final String proxyAddress;
    
    private final ChannelHandlerContext ctx;
    
    private final ChannelInfo channelInfo;
    
    private final AtomicLong recvSeq;
    
    private volatile boolean connected;
    
    private volatile boolean connectTimeout;
    
    private final Object connectLock;
    
    public Replier(String appSocketClient, String proxyAddress, ChannelHandlerContext ctx) {
        this.appSocketClient = appSocketClient;
        this.proxyAddress = proxyAddress;
        this.ctx = ctx;
        this.channelInfo = ProxyHelper.getChannelInfo(ctx);
        this.recvSeq = new AtomicLong(-1);
        this.connected = false;
        this.connectTimeout = true;
        this.connectLock = new Object();
    }
    
    public void send(long sendSeq, byte[] data) {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        buf.writeBytes(data);
        ctx.writeAndFlush(buf);
        log.info("成功发送数据至:[{}]，序号:[{}],长度:[{}]",
                channelInfo.getRemoteAddress(), sendSeq, data.length);
    }
    
    public long receive() {
        return recvSeq.incrementAndGet();
    }
    
    public void close() {
        ctx.close();
    }
    
    public ChannelInfo getChannelInfo() {
        return channelInfo;
    }
    
    public long getRecvSeq() {
        return recvSeq.get();
    }
    
    public boolean isConnected() {
        return connected;
    }
    
    public void setConnected(boolean connected) {
        this.connected = connected;
    }
    
    public Object getConnectLock() {
        return connectLock;
    }
    
}
