package com.x.bridge.proxy.server;

import com.x.bridge.core.IServerListener;
import com.x.bridge.core.SocketConfig;
import com.x.bridge.data.ChannelInfo;
import com.x.bridge.data.ProxyConfig;
import com.x.bridge.data.ReplierManager;
import com.x.bridge.proxy.ProxyConfigManager;
import com.x.bridge.proxy.ProxyManager;
import com.x.bridge.proxy.Replier;
import com.x.bridge.util.SocketHelper;
import com.x.doraemon.util.Strings;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.log4j.Log4j2;

/**
 * @Desc TODO
 * @Date 2020/10/22 01:09
 * @Author AD
 */
@Log4j2
public final class ServerListener implements IServerListener {

    private final ReplierManager replierManager;

    public ServerListener(ReplierManager replierManager) {
        this.replierManager = replierManager;
    }

    @Override
    public void onServerStart(SocketConfig config) {
        log.info("服务启动:[{}:{}]", config.getIp(), config.getPort());
    }

    @Override
    public void onServerStartError(Throwable e) {
        log.error("服务启动异常:{}", Strings.getExceptionTrace(e));
    }

    @Override
    public void onServerStop(SocketConfig config) {
        log.info("服务关闭:[{}:{}]", config.getIp(), config.getPort());
    }

    @Override
    public void active(ChannelHandlerContext ctx) throws Exception {
        ChannelInfo ch = SocketHelper.getChannelInfo(ctx);
        Replier replier = new Replier(ch.getRemoteAddress(),ctx);
        replier.received();
        ProxyConfig cfg = ProxyConfigManager.getProxyConfig(ch.getLocalPort());
        if (cfg.isAllowClient(ch.getRemoteIP())) {
            Proxy proxy = ProxyManager.getProxy(ch.getLocalPort());
            replierManager.addReplier(ch.getRemoteAddress(), replier);
            if (!proxy.connectRequest(replier)) {
                replierManager.removeReplier(ch.getRemoteAddress());
                replier.close();
                log.info("另一端代理与目标服务器:[{}]连接建立失败，当前连接:[{}]将关闭", ch.getRemoteAddress());
            } else {
                replier.setConnected(true);
                log.info("连接:{} 建立成功", ch.getRemoteAddress());
            }
        } else {
            replier.close();
            log.info("非法客户端:[{}]企图建立与代理:[{}]建立连接,已关闭", ch.getLocalAddress(), ch.getRemoteAddress());
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
                replier.received();
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
            replier.received();
            Proxy proxy = ProxyManager.getProxy(ch.getLocalPort());
            byte[] data = SocketHelper.readData(buf);
            proxy.sendToProxy(remoteAddress, replier.getRecvSeq(), data);
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
