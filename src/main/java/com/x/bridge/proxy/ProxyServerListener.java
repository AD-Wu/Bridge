package com.x.bridge.proxy;

import com.x.bridge.command.Command;
import com.x.bridge.data.ChannelInfo;
import com.x.bridge.data.ProxyConfig;
import com.x.bridge.data.ReplierManager;
import com.x.doraemon.util.Strings;
import com.x.bridge.core.ISocketListener;
import com.x.bridge.core.SocketConfig;
import com.x.bridge.util.SocketHelper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @Desc TODO
 * @Date 2020/10/22 01:09
 * @Author AD
 */
@Log4j2
public class ProxyServerListener implements ISocketListener {
    
    private final ReplierManager replierManager;
    
    public ProxyServerListener(ReplierManager replierManager) {
        this.replierManager = replierManager;
    }
    
    @Override
    public void onStart(SocketConfig config) {
        log.info("服务启动，IP={},Port={}", config.getIp(), config.getPort());
    }
    
    @Override
    public void onStartError(Throwable e) {
        log.error("服务启动异常:{}", Strings.getExceptionTrace(e));
    }
    
    @Override
    public void onStop(SocketConfig config) {
        log.info("服务关闭，IP={},Port={}", config.getIp(), config.getPort());
    }
    
    @Override
    public void active(ChannelHandlerContext ctx) throws Exception {
        Replier replier = new Replier(ctx);
        replier.recvSeqIncrement();
        ChannelInfo ch = replier.getChannelInfo();
        ProxyConfig cfg = ProxyConfigManager.getProxyConfig(ch.getLocalPort());
        if (cfg.isAllowClient(ch.getRemoteIP())) {
            Proxy proxy = ProxyManager.getProxy(ch.getLocalPort());
            if (!proxy.connectRequest(replier)) {
                replier.close();
                log.info("另一端代理与目标服务器:{}连接建立失败，当前连接:{} 将关闭", ch.getRemoteAddress());
            } else {
                replier.setConnected(true);
                replierManager.addReplier(ch.getRemoteAddress(), replier);
                log.info("连接:{} 建立成功", ch.getRemoteAddress());
            }
        } else {
            replier.close();
            log.info("代理:{},收到非法IP:{}企图建立连接,已关闭", ch.getLocalAddress(), ch.getRemoteAddress());
        }
    }
    
    @Override
    public void inActive(ChannelHandlerContext ctx) throws Exception {
        ChannelInfo ch = SocketHelper.getChannelInfo(ctx);
        String remote = ch.getRemoteAddress();
        Replier replier = replierManager.removeReplier(remote);
        if (replier != null) {
            if (replier.isConnected()) {
                Proxy proxy = ProxyManager.getProxy(ch.getLocalPort());
                replier.recvSeqIncrement();
                proxy.disconnect(replier);
                replier.close();
                log.info("连接:{} 关闭，通知另一端代理关闭", remote);
            } else {
                log.info("连接:{} 关闭，另一端代理未建立连接，不通知关闭", remote);
            }
        }
    }
    
    @Override
    public void receive(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
        ChannelInfo ch = SocketHelper.getChannelInfo(ctx);
        String remoteAddress = ch.getRemoteAddress();
        Replier replier = replierManager.getReplier(remoteAddress);
        if (replier != null) {
            replier.recvSeqIncrement();
            Proxy proxy = ProxyManager.getProxy(ch.getLocalPort());
            byte[] data = SocketHelper.readData(buf);
            proxy.receive(replier, data);
            log.info("代理:{} 接收来自客户端:{} 的数据，序号:{},数据长度:{}",
                    ch.getLocalAddress(), remoteAddress, replier.getRecvSeq(), data.length);
        }
    }
    
    @Override
    public void timeout(ChannelHandlerContext ctx, IdleStateEvent event) throws Exception {
    
    }
    
    @Override
    public void error(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    
    }
    
}
