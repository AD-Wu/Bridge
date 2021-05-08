package com.x.bridge.proxy.core;

import com.x.bridge.data.ChannelInfo;
import com.x.bridge.util.ProxyHelper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Desc 通道应答对象
 * @Date 2020/10/22 19:03
 * @Author AD
 */
@Log4j2
public class Replier {
    
    private String appClient;
    private String proxyServer;
    
    private String proxyClient;
    private String appServer;
    
    private final ChannelHandlerContext ctx;
    private final ChannelInfo channelInfo;
    private final AtomicLong recvSeq = new AtomicLong(0);
    private final AtomicLong sendSeq = new AtomicLong(1);
    private volatile boolean connected = false;
    private volatile boolean connectTimeout = true;
    private final Object connectLock = new Object();

    private final Map<Long, byte[]> dataCache = new ConcurrentHashMap<>();
    
    public static Replier getServerReplier(ChannelHandlerContext ctx) {
        ChannelInfo ch = ProxyHelper.getChannelInfo(ctx);
        Replier replier = new Replier(ctx);
        replier.setAppClient(ch.getRemoteAddress());
        replier.setProxyServer(ch.getLocalAddress());
        return replier;
    }
    
    public static Replier getClientReplier(ChannelHandlerContext ctx) {
        ChannelInfo ch = ProxyHelper.getChannelInfo(ctx);
        Replier replier = new Replier(ctx);
        replier.setProxyClient(ch.getLocalAddress());
        replier.setAppServer(ch.getRemoteAddress());
        return replier;
    }
    
    private Replier(ChannelHandlerContext ctx){
        this.ctx = ctx;
        this.channelInfo = ProxyHelper.getChannelInfo(ctx);
    }
    
    public void send(long recvSeq, byte[] data) {
        if (recvSeq > nextSendSeq()) {
            dataCache.put(recvSeq, data);
        } else {
            if (recvSeq == nextSendSeq()) {
                synchronized (this) {
                    if (recvSeq == nextSendSeq()) {
                        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
                        buf.writeBytes(data);
                        ctx.writeAndFlush(buf);
                        this.sendSeq.incrementAndGet();
                        log.info("成功发送数据至:[{}]，序号:[{}],长度:[{}]",
                                channelInfo.getRemoteAddress(), recvSeq, data.length);
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
    
    public boolean isConnected() {
        return connected;
    }
    
    public void setConnected(boolean connected) {
        this.connected = connected;
    }
    
    public Object getConnectLock() {
        return connectLock;
    }
    
    public String getAppClient() {
        return appClient;
    }
    
    public void setAppClient(String appClient) {
        this.appClient = appClient;
    }
    
    public String getProxyServer() {
        return proxyServer;
    }
    
    public void setProxyServer(String proxyServer) {
        this.proxyServer = proxyServer;
    }
    
    public String getProxyClient() {
        return proxyClient;
    }
    
    public void setProxyClient(String proxyClient) {
        this.proxyClient = proxyClient;
    }
    
    public String getAppServer() {
        return appServer;
    }
    
    public void setAppServer(String appServer) {
        this.appServer = appServer;
    }
    
    public ChannelInfo getChannelInfo() {
        return channelInfo;
    }
    
    public boolean isConnectTimeout() {
        return connectTimeout;
    }
    
    public void setConnectTimeout(boolean connectTimeout) {
        this.connectTimeout = connectTimeout;
    }
    
}
