package com.x.bridge.proxy;

import com.x.bridge.data.ChannelInfo;
import com.x.bridge.util.SocketHelper;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @Desc TODO
 * @Date 2020/10/22 19:03
 * @Author AD
 */
@Data
public final class Replier {

    private final ChannelHandlerContext ctx;

    private final ChannelInfo channelInfo;

    private final AtomicLong recvSeq;

    private volatile boolean connected;

    private volatile boolean connectTimeout;

    private final Object connectLock;

    public Replier(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        this.channelInfo = SocketHelper.getChannelInfo(ctx);
        this.recvSeq = new AtomicLong(-1);
        this.connected = false;
        this.connectLock = new Object();
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

    public long recvSeqIncrement() {
        return recvSeq.incrementAndGet();
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
