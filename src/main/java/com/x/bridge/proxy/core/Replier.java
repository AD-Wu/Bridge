package com.x.bridge.proxy.core;

import com.x.bridge.proxy.data.ChannelInfo;
import com.x.bridge.proxy.util.ProxyHelper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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

    private final AtomicLong sendSeq;

    private volatile boolean connected;

    private volatile boolean connectTimeout;

    private final Object connectLock;

    private final Map<Long, byte[]> dataCache;

    public Replier(String appSocketClient, String proxyAddress, ChannelHandlerContext ctx) {
        this.appSocketClient = appSocketClient;
        this.proxyAddress = proxyAddress;
        this.ctx = ctx;
        this.channelInfo = ProxyHelper.getChannelInfo(ctx);
        this.recvSeq = new AtomicLong(0);
        this.sendSeq = new AtomicLong(1);
        this.connected = false;
        this.connectTimeout = true;
        this.connectLock = new Object();
        this.dataCache = new ConcurrentHashMap<>();
    }

    public void send(long sendSeq, byte[] data) {
        if (sendSeq > nextSendSeq()) {
            dataCache.put(sendSeq, data);
        } else {
            if (sendSeq == nextSendSeq()) {
                synchronized (this) {
                    if (sendSeq == nextSendSeq()) {
                        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
                        buf.writeBytes(data);
                        ctx.writeAndFlush(buf);
                        this.sendSeq.incrementAndGet();
                        log.info("成功发送数据至:[{}]，序号:[{}],长度:[{}]",
                                channelInfo.getRemoteAddress(), sendSeq, data.length);
                    }
                }
                byte[] next = dataCache.remove(nextSendSeq());
                if (next != null) {
                    send(nextSendSeq(), next);
                }

            }
        }
    }

    private long nextSendSeq() {
        return sendSeq.get();
    }

    public void receive() {
        recvSeq.incrementAndGet();
    }

    public long getRecvSeq() {
        return recvSeq.get();
    }

    public void close() {
        dataCache.clear();
        ctx.close();
    }

    public ChannelInfo getChannelInfo() {
        return channelInfo;
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
