package com.x.bridge.proxy.client;

import com.x.bridge.common.ISocketListener;
import com.x.bridge.proxy.core.Replier;
import com.x.bridge.data.ChannelInfo;
import com.x.bridge.proxy.core.MessageType;
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
    
    private final String appSocketClient;
    
    private final String proxyServer;
    
    private final ProxyClient proxyClient;
    
    public ProxyClientListener(String appSocketClient, String proxyServer, ProxyClient proxyClient) {
        this.appSocketClient = appSocketClient;
        this.proxyServer = proxyServer;
        this.proxyClient = proxyClient;
    }
    
    @Override
    public void active(ChannelHandlerContext ctx) throws Exception {
        // 生成应答对象
        Replier replier = new Replier(appSocketClient, proxyServer, ctx);
        // 获取通道信息
        ChannelInfo ch = replier.getChannelInfo();
        // 接收数据（从建立连接开始，seq就开始递增）
        replier.receive();
        // 设置当前会话连接状态
        replier.setConnected(true);
        // 设置代理客户端地址
        replier.setProxyClient(ch.getLocalAddress());
        // 设置app服务端地址
        replier.setAppSocketServer(ch.getRemoteAddress());
        // 日志记录
        log.info("连接建立,客户端:[{}]，代理(客户端):[{}]，服务端:[{}]",
                appSocketClient, replier.getProxyClient(), replier.getAppSocketServer());
        // 管理应答对象
        proxyClient.addReplier(appSocketClient, replier);
        // 获取代理客户端，通知另一端代理（服务端）连接成功
        proxyClient.connectSuccess(replier);
    }
    
    @Override
    public void inActive(ChannelHandlerContext ctx) throws Exception {
        // 移除应答对象
        Replier replier = proxyClient.removeReplier(appSocketClient);
        // 如果是应用在代理服务端主动断开，先执行Disconnect命令，此时会为空
        if (replier != null) {
            // 接收数据，seq递增
            replier.receive();
            // 关闭应答对象
            replier.close();
            // 设置连接关闭状态
            replier.setConnected(false);
            // 获取通道信息
            ChannelInfo ch = replier.getChannelInfo();
            // 日志记录
            log.info("连接关闭，客户端:[{}]，代理(客户端):[{}]，服务端:[{}]，通知代理(服务端)关闭",
                    appSocketClient, ch.getLocalAddress(), ch.getRemoteAddress());
            // 通知另一端（服务端）关闭连接
            proxyClient.disconnect(replier, MessageType.ClientToServer);
        }
    }
    
    @Override
    public void receive(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
        // 获取应答对象
        Replier replier = proxyClient.getReplier(appSocketClient);
        if (replier != null) {
            // 接收数据，seq递增
            replier.receive();
            // 转发来自目标服务端的数据
            byte[] data = ProxyHelper.readData(buf);
            // 获取通道信息
            ChannelInfo ch = replier.getChannelInfo();
            // 日志记录
            log.info("接收数据，客户端:[{}]，代理(客户端):[{}]，服务端:[{}]，序号:[{}],数据长度:[{}]",
                    appSocketClient, ch.getLocalAddress(), ch.getRemoteAddress(),
                    replier.getRecvSeq(), data.length);
            // 发送数据
            proxyClient.send(replier, MessageType.ClientToServer, data);
        }
    }
    
    @Override
    public void timeout(ChannelHandlerContext ctx, IdleStateEvent event) throws Exception {
    
    }
    
    @Override
    public void error(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Replier replier = proxyClient.getReplier(appSocketClient);
        if (replier == null) {
            replier = new Replier(appSocketClient, proxyServer, ctx);
        }
        proxyClient.connectFailed(replier);
        log.error(StringHelper.getExceptionTrace(cause));
    }
    
}
