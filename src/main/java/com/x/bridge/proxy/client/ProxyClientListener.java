package com.x.bridge.proxy.client;

import com.x.bridge.common.ISocketListener;
import com.x.bridge.data.ChannelData;
import com.x.bridge.proxy.command.*;
import com.x.bridge.proxy.core.Proxy;
import com.x.bridge.proxy.core.Replier;
import com.x.bridge.util.ProxyHelper;
import com.x.doraemon.util.StringHelper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.log4j.Log4j2;

/**
 * @Desc
 * @Date 2020/10/23 09:31
 * @Author AD
 */
@Log4j2
public final class ProxyClientListener implements ISocketListener {

    private final String appClient;
    private final String proxyServer;
    private final Proxy<ChannelData> proxyClient;

    public ProxyClientListener(String appClient, String proxyServer, Proxy<ChannelData> proxyClient) {
        this.appClient = appClient;
        this.proxyServer = proxyServer;
        this.proxyClient = proxyClient;
    }

    @Override
    public void active(ChannelHandlerContext ctx) throws Exception {
        // 生成应答对象
        Replier replier = Replier.getClientReplier(ctx, appClient, proxyServer);
        // 接收数据（从建立连接开始，seq就开始递增）
        replier.receive();
        // 设置当前会话连接状态
        replier.setConnected(true);
        // 日志记录
        log.info("连接建立,客户端:[{}]，代理(客户端):[{}]，服务端:[{}]", appClient, replier.getProxyClient(), replier.getAppServer());
        // 管理应答对象
        proxyClient.addReplier(appClient, replier);
        // 获取代理客户端，通知另一端代理（服务端）连接成功
        proxyClient.send(SyncConnectSuccess.getData(replier));
    }

    @Override
    public void inActive(ChannelHandlerContext ctx) throws Exception {
        // 移除应答对象
        Replier replier = proxyClient.removeReplier(appClient);
        // 如果是应用在代理服务端主动断开，先执行Disconnect命令，此时会为空
        if (replier != null) {
            // 接收数据，seq递增
            replier.receive();
            // 关闭应答对象
            replier.close();
            // 设置连接关闭状态
            replier.setConnected(false);
            // 日志记录
            log.info("连接关闭，客户端:[{}]，代理(客户端):[{}]，服务端:[{}]，通知代理(服务端)关闭",
                    appClient, replier.getProxyClient(), replier.getAppServer());
            // 通知另一端（服务端）关闭连接
            proxyClient.send(Disconnect.getData(replier));

        }
    }

    @Override
    public void receive(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
        // 获取应答对象
        Replier replier = proxyClient.getReplier(appClient);
        if (replier != null) {
            // 接收数据，seq递增
            replier.receive();
            // 转发来自目标服务端的数据
            byte[] data = ProxyHelper.readData(buf);
            // 日志记录
            log.info("接收数据，客户端:[{}]，代理(客户端):[{}]，服务端:[{}]，序号:[{}],数据长度:[{}]",
                    appClient, replier.getProxyClient(), replier.getAppServer(),
                    replier.getRecvSeq(), data.length);
            // 发送数据
            proxyClient.send(SendData.getData(replier, data));
        }
    }

    @Override
    public void timeout(ChannelHandlerContext ctx, IdleStateEvent event) throws Exception {
        Replier replier = proxyClient.getReplier(appClient);
        if (replier == null) {
            replier = Replier.getClientReplier(ctx, appClient, proxyServer);
        }
        // 日志记录
        log.error("连接超时，客户端:[{}]，代理(客户端):[{}]，服务端:[{}]，超时时间:{}",
                appClient, replier.getProxyClient(), replier.getAppServer(),
                proxyClient.getConfig().getConnectTimeout());
        proxyClient.send(SyncConnectTimeout.getData(replier));

    }

    @Override
    public void error(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Replier replier = proxyClient.getReplier(appClient);
        if (replier == null) {
            replier = Replier.getClientReplier(ctx, appClient, proxyServer);
        }
        proxyClient.send(SyncConnectError.getData(replier, StringHelper.getExceptionTrace(cause)));
        log.error(StringHelper.getExceptionTrace(cause));
    }

}
