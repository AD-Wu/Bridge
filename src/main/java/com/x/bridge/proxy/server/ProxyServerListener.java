package com.x.bridge.proxy.server;

import com.x.bridge.common.ISocketListener;
import com.x.bridge.data.ChannelData;
import com.x.bridge.proxy.command.Disconnect;
import com.x.bridge.proxy.command.SendData;
import com.x.bridge.proxy.command.SyncConnectRequest;
import com.x.bridge.proxy.core.Proxy;
import com.x.bridge.proxy.core.Replier;
import com.x.bridge.util.ProxyHelper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.log4j.Log4j2;

/**
 * @Desc 代理服务端监听器
 * @Date 2020/10/22 01:09
 * @Author AD
 */
@Log4j2
public final class ProxyServerListener implements ISocketListener {
    
    private final Proxy<ChannelData> server;
    private final SendData sendDataCmd;
    
    public ProxyServerListener(Proxy<ChannelData> server) {
        this.server = server;
        this.sendDataCmd = new SendData();
    }
    
    @Override
    public void active(ChannelHandlerContext ctx) throws Exception {
        // 创建应答对象
        Replier replier = Replier.getServerReplier(ctx);
        // 设置app服务器地址
        replier.setAppServer(server.getConfig().getAppServer());
        // 递增接收数据的序号
        replier.receive();
        // 是否允许连接
        if (server.isAccept(replier.getAppClient())) {
            // 管理应答对象
            server.addReplier(replier.getAppClient(), replier);
            // 日志记录
            log.info("连接请求建立，客户端:[{}]，代理(服务端):[{}]，服务端:[{}]",
                    replier.getAppClient(), replier.getProxyServer(), replier.getAppServer());
            // 向代理(客户端)发送连接请求
            SyncConnectRequest serverConnect = new SyncConnectRequest();
            ChannelData data = serverConnect.get(replier);
            serverConnect.send(this.server, data);
        } else {
            // 非法连接，关闭通道
            replier.close();
            log.info("非法客户端，客户端:[{}]，代理(服务端):[{}]，服务端:[{}]",
                    replier.getAppClient(), replier.getProxyServer(), server.getConfig().getAppServer());
        }
    }
    
    @Override
    public void inActive(ChannelHandlerContext ctx) throws Exception {
        // 获取socket客户端
        String appClient = ProxyHelper.getChannelInfo(ctx).getRemoteAddress();
        // 移除应答者
        Replier replier = server.removeReplier(appClient);
        // 判断是否有效
        if (replier != null) {
            // 已连接状态
            if (replier.isConnected()) {
                // 日志记录
                log.info("连接关闭，客户端:[{}]，代理(服务端):[{}]，服务端:[{}]，通知代理(客户端)关闭",
                        appClient, replier.getProxyServer(), server.getConfig().getAppServer());
                // 递增接收序号
                replier.receive();
                // 通知代理(客户端)关闭连接
                Disconnect disconnect = new Disconnect();
                ChannelData data = disconnect.get(replier);
                disconnect.send(this.server, data);
                // 关闭通道
                replier.close();
            } else {
                log.info("连接关闭，客户端:[{}]，代理(服务端):[{}]，服务端:[{}]，代理(客户端)未建立连接，无需通知",
                        appClient, replier.getProxyServer(), server.getConfig().getAppServer());
            }
        }
    }
    
    @Override
    public void receive(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
        // 获取socket客户端
        String appClient = ProxyHelper.getChannelInfo(ctx).getRemoteAddress();
        // 获取应答者
        Replier replier = server.getReplier(appClient);
        // 判断是否有效
        if (replier != null) {
            // 递增接收序号
            replier.receive();
            // 读取数据
            byte[] data = ProxyHelper.readData(buf);
            // 日志记录
            log.info("接收数据，客户端:[{}]，代理(服务端):[{}]，服务端:[{}]，序号:[{}],数据长度:[{}]",
                    appClient, replier.getProxyServer(), server.getConfig().getAppServer(),
                    replier.getRecvSeq(), data.length);
            // 发送给代理(客户端)
            ChannelData cd = sendDataCmd.get(replier);
            cd.setData(data);
            sendDataCmd.send(this.server, cd);
        }
    }
    
    @Override
    public void timeout(ChannelHandlerContext ctx, IdleStateEvent event) throws Exception {
    
    }
    
    @Override
    public void error(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    
    }
    
}
