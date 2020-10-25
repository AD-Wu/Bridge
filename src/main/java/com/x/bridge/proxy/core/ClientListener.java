package com.x.bridge.proxy.core;

import com.x.bridge.core.ISocketListener;
import com.x.bridge.proxy.data.ChannelInfo;
import com.x.bridge.proxy.data.MessageType;
import com.x.bridge.util.SocketHelper;
import com.x.doraemon.util.Strings;
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
public final class ClientListener implements ISocketListener {
    
    private final String appSocketClient;
    
    private final String proxyAddress;
    
    private final ProxyClient proxyClient;
    
    public ClientListener(String appSocketClient, String proxyAddress, ProxyClient proxyClient) {
        this.appSocketClient = appSocketClient;
        this.proxyAddress = proxyAddress;
        this.proxyClient = proxyClient;
    }
    
    @Override
    public void active(ChannelHandlerContext ctx) throws Exception {
        // 生成应答对象
        Replier replier = new Replier(appSocketClient, proxyAddress, ctx);
        // 接收数据（从建立连接开始，seq就开始递增）
        replier.receive();
        // 设置当前会话连接状态
        replier.setConnected(true);
        // 获取代理客户端，通知另一端代理（服务端）连接成功
        proxyClient.connectSuccess(replier);
        // 管理应答对象
        proxyClient.addReplier(appSocketClient, replier);
        // 获取通道信息
        ChannelInfo ch = replier.getChannelInfo();
        // 日志记录
        log.info("连接建立,客户端:[{}]，代理(客户端):[{}]，服务端:[{}]", appSocketClient, ch.getLocalAddress(), ch.getRemoteAddress());
        
    }
    
    @Override
    public void inActive(ChannelHandlerContext ctx) throws Exception {
        // 移除应答对象
        Replier replier = proxyClient.removeReplier(appSocketClient);
        // 接收数据，seq递增
        replier.receive();
        // 关闭应答对象
        replier.close();
        // 设置连接关闭状态
        replier.setConnected(false);
        // 通知另一端（服务端）关闭连接
        proxyClient.disconnect(replier, MessageType.ClientToServer);
        // 获取通道信息
        ChannelInfo ch = replier.getChannelInfo();
        // 日志记录
        log.info("连接关闭，客户端:[{}]，代理(客户端):[{}]，服务端:[{}]，通知代理(服务端)关闭",
                appSocketClient, ch.getLocalAddress(), ch.getRemoteAddress());
        
    }
    
    @Override
    public void receive(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
        // 获取应答对象
        Replier replier = proxyClient.getReplier(appSocketClient);
        // 接收数据，seq递增
        replier.receive();
        // 转发来自目标服务端的数据
        byte[] data = SocketHelper.readData(buf);
        proxyClient.send(replier, MessageType.ClientToServer, data);
        // 获取通道信息
        ChannelInfo ch = replier.getChannelInfo();
        // 日志记录
        log.info("接收数据，客户端:[{}]，代理(客户端):[{}]，服务端:[{}]，序号:{},数据长度:{}",
                appSocketClient, ch.getLocalAddress(),ch.getRemoteAddress(),
                replier.getRecvSeq(), data.length);
    }
    
    @Override
    public void timeout(ChannelHandlerContext ctx, IdleStateEvent event) throws Exception {
    
    }
    
    @Override
    public void error(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Replier replier = proxyClient.getReplier(appSocketClient);
        if (replier == null) {
            replier = new Replier(appSocketClient, proxyAddress, ctx);
        }
        proxyClient.connectFailed(replier);
        log.error(Strings.getExceptionTrace(cause));
    }
    
}
