package com.x.bridge.proxy.client;

import com.x.bridge.core.ISocketListener;
import com.x.bridge.proxy.ReplierManager;
import com.x.bridge.proxy.core.Replier;
import com.x.bridge.proxy.core.Proxy;
import com.x.bridge.util.SocketHelper;
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
    
    private final ReplierManager replierManager;
    
    public ClientListener(String appSocketClient, ReplierManager replierManager) {
        this.appSocketClient = appSocketClient;
        this.replierManager = replierManager;
    }
    
    @Override
    public void active(ChannelHandlerContext ctx) throws Exception {
        // 生成应答对象
        Replier replier = new Replier(appSocketClient, ctx);
        // 接收数据（从建立连接开始，seq就开始递增）
        replier.receive();
        // 设置当前会话连接状态
        replier.setConnected(true);
        // 获取代理对象，通知另一端代理（服务端）连接成功
        Proxy proxy = replierManager.getProxy();
        proxy.connectSuccess(replier);
        // 管理应答对象
        replierManager.addReplier(this.appSocketClient, replier);
        // 日志记录
        log.info("应用客户端:[{}]连接建立成功", appSocketClient);
    }
    
    @Override
    public void inActive(ChannelHandlerContext ctx) throws Exception {
        // 移除应答对象
        Replier replier = replierManager.removeReplier(appSocketClient);
        // 接收数据，seq递增
        replier.receive();
        // 关闭应答对象
        replier.close();
        // 设置连接关闭状态
        replier.setConnected(false);
        // 获取代理对象，通知另一端（服务端）关闭连接
        Proxy proxy = replierManager.getProxy();
        proxy.disconnect(replier);
        // 日志记录
        log.info("应用服务器:[{}]主动关闭客户端连接,通知服务端代理关闭应用客户端连接:[{}]",
                replier.getChannelInfo().getRemoteAddress(), appSocketClient);
        
    }
    
    @Override
    public void receive(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
        // 获取应答对象
        Replier replier = replierManager.getReplier(appSocketClient);
        // 接收数据，seq递增
        replier.receive();
        // 获取代理对象，转发来自服务端端数据
        Proxy proxy = replierManager.getProxy();
        byte[] data = SocketHelper.readData(buf);
        proxy.sendToProxy(appSocketClient, replier.getRecvSeq(), data);
        // 日志记录
        log.info("客户端:[[]]收到来自应用服务器:[{}]的数据，序号:{},数据长度:{}",
                appSocketClient, replier.getChannelInfo().getRemoteAddress(),
                replier.getRecvSeq(), data.length);
    }
    
    @Override
    public void timeout(ChannelHandlerContext ctx, IdleStateEvent event) throws Exception {
    
    }
    
    @Override
    public void error(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    
    }
    
}
