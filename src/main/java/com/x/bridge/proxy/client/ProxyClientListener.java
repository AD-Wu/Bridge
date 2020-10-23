package com.x.bridge.proxy.client;

import com.x.bridge.core.ISocketListener;
import com.x.bridge.data.ReplierManager;
import com.x.bridge.proxy.Replier;
import com.x.bridge.proxy.server.Proxy;
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

    private final String proxyKey;
    private final ReplierManager replierManager;

    public ProxyClientListener(String proxyKey, ReplierManager replierManager) {
        this.proxyKey = proxyKey;
        this.replierManager = replierManager;
    }

    @Override
    public void active(ChannelHandlerContext ctx) throws Exception {
        // 生成应答对象
        Replier replier = new Replier(ctx);
        // 接收数据（从建立连接开始，seq就开始递增）
        replier.received();
        // 设置当前会话连接状态
        replier.setConnected(true);
        // 获取代理对象，通知另一端代理（服务端）连接成功
        Proxy proxy = replierManager.getProxy();
        proxy.connectSuccess(replier);
        // 管理应答对象
        replierManager.addReplier(this.proxyKey, replier);

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
