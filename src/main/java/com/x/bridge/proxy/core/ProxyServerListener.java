package com.x.bridge.proxy.core;

import com.x.bridge.common.IServerListener;
import com.x.bridge.common.SocketConfig;
import com.x.bridge.proxy.data.ChannelInfo;
import com.x.bridge.proxy.data.MessageType;
import com.x.bridge.proxy.util.ProxyHelper;
import com.x.doraemon.util.Strings;
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
public final class ProxyServerListener implements IServerListener {

    private final ProxyServer server;

    public ProxyServerListener(ProxyServer proxyServer) {
        this.server = proxyServer;
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
        // 获取通道信息
        ChannelInfo ch = ProxyHelper.getChannelInfo(ctx);
        // 创建应答对象
        Replier replier = new Replier(ch.getRemoteAddress(), ch.getLocalAddress(), ctx);
        // 递增接收数据的序号
        //replier.receive();
        // 是否允许连接
        if (server.isAccept(ch.getRemoteIP())) {
            // 管理应答对象
            server.addReplier(ch.getRemoteAddress(), replier);
            // 日志记录
            log.info("连接请求建立，客户端:[{}]，代理(服务端):[{}]，服务端:[{}]",
                    ch.getRemoteAddress(), ch.getLocalAddress(), server.getConfig().getTargetAddress());
            // 向代理(客户端)发送连接请求
            if (!server.connectRequest(replier)) {
                // 连接建立失败，移除应答者
                server.removeReplier(ch.getRemoteAddress());
                // 关闭通道
                replier.close();
                // 判断是否连接超时
                if (!replier.isConnectTimeout()) {
                    log.info("连接建立失败，客户端:[{}]，代理(服务端):[{}]，服务端:[{}]",
                            ch.getRemoteAddress(), ch.getLocalAddress(), server.getConfig().getTargetAddress());
                }
            } else {
                // 连接成功，设置连接状态
                replier.setConnected(true);
                log.info("连接建立成功，客户端:[{}]，代理(服务端):[{}]，服务端:[{}]",
                        ch.getRemoteAddress(), ch.getLocalAddress(), server.getConfig().getTargetAddress());
            }
        } else {
            // 非法连接，关闭通道
            replier.close();
            log.info("非法客户端，客户端:[{}]，代理(服务端):[{}]，服务端:[{}]",
                    ch.getRemoteAddress(), ch.getLocalAddress(), server.getConfig().getTargetAddress());
        }
    }

    @Override
    public void inActive(ChannelHandlerContext ctx) throws Exception {
        // 获取通道信息
        ChannelInfo ch = ProxyHelper.getChannelInfo(ctx);
        // 获取socket客户端
        String remote = ch.getRemoteAddress();
        // 移除应答者
        Replier replier = server.removeReplier(remote);
        // 判断是否有效
        if (replier != null) {
            // 已连接状态
            if (replier.isConnected()) {
                // 日志记录
                log.info("连接关闭，客户端:[{}]，代理(服务端):[{}]，服务端:[{}]，通知代理(客户端)关闭",
                        remote, ch.getLocalAddress(), server.getConfig().getTargetAddress());
                // 递增接收序号
                //replier.receive();
                // 通知代理(客户端)关闭连接
                server.disconnect(replier, MessageType.ServerToClient);
                // 关闭通道
                replier.close();
            } else {
                log.info("连接关闭，客户端:[{}]，代理(服务端):[{}]，服务端:[{}]，代理(客户端)未建立连接，无需通知",
                        remote, ch.getLocalAddress(), server.getConfig().getTargetAddress());
            }
        }
    }

    @Override
    public void receive(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
        // 获取通道信息
        ChannelInfo ch = ProxyHelper.getChannelInfo(ctx);
        // 获取socket客户端
        String remote = ch.getRemoteAddress();
        // 获取应答者
        Replier replier = server.getReplier(remote);
        // 判断是否有效
        if (replier != null) {
            // 递增接收序号
            replier.receive();
            // 读取数据
            byte[] data = ProxyHelper.readData(buf);
            // 日志记录
            log.info("接收数据，客户端:[{}]，代理(服务端):[{}]，服务端:[{}]，序号:[{}],数据长度:[{}]",
                    remote, ch.getLocalAddress(), server.getConfig().getTargetAddress(),
                    replier.getRecvSeq(), data.length);
            // 发送给代理(客户端)
            server.send(replier, MessageType.ServerToClient, data);
        }
    }

    @Override
    public void timeout(ChannelHandlerContext ctx, IdleStateEvent event) throws Exception {

    }

    @Override
    public void error(ChannelHandlerContext ctx, Throwable cause) throws Exception {

    }

}
