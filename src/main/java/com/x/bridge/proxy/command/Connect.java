package com.x.bridge.proxy.command;

import com.x.bridge.common.SocketClient;
import com.x.bridge.common.SocketConfig;
import com.x.bridge.data.ChannelData;
import com.x.bridge.proxy.client.ProxyClient;
import com.x.bridge.proxy.client.ProxyClientListener;
import com.x.bridge.proxy.command.core.ICommand;
import com.x.bridge.proxy.core.Command;
import com.x.bridge.proxy.core.Proxy;
import com.x.bridge.proxy.core.Replier;
import com.x.bridge.util.ProxyHelper;
import lombok.extern.log4j.Log4j2;

/**
 * @Desc
 * @Date 2021/5/3 19:19
 * @Author AD
 */
@Log4j2
public class Connect implements ICommand<ChannelData> {
    
    @Override
    public void request(Proxy<ChannelData> proxy, ChannelData cd) {
        // 获取应用客户端地址
        String appSocket = cd.getAppSocketClient();
        // 获取应答者
        Replier replier = proxy.getReplier(appSocket);
        // 未建立建立
        if (replier == null) {
            // 创建socket客户端连接目标服务器
            String ip = ProxyHelper.getIP(cd.getAppSocketServer());
            int port = ProxyHelper.getPort(cd.getAppSocketServer());
            SocketClient socket = new SocketClient(
                    proxy.name(),
                    new SocketConfig(ip, port),
                    new ProxyClientListener(appSocket, cd.getProxyServer(), (ProxyClient) proxy));
            socket.start();
        } else {
            log.info("代理服务器:[{}],连接:[{}]已存在，不再重新建立",
                    cd.getProxyServer(), cd.getAppSocketClient());
        }
    }
    
    @Override
    public void response(Proxy<ChannelData> proxy, ChannelData cd) {
        // 获取应用客户端地址
        String appSocket = cd.getAppSocketClient();
        // 获取应答者
        Replier replier = proxy.getReplier(appSocket);
        if (replier != null) {
            // 获取应答者连接建立锁
            Object connectLock = replier.getConnectLock();
            // 获取命令
            Command cmd = cd.getCommand();
            if (cmd == Command.ConnectSuccess) {
                // 通知连接建立成功
                synchronized (connectLock) {
                    replier.setConnected(true);
                    replier.setConnectTimeout(false);
                    replier.setProxyClient(cd.getProxyClient());
                    connectLock.notify();
                }
            } else {
                // 通知连接建立失败
                synchronized (connectLock) {
                    replier.setConnected(false);
                    connectLock.notify();
                }
                log.info("应用客户端:[{}]与目标服务器:[{}]连接建立失败", cd.getAppSocketClient(), cd.getAppSocketServer());
            }
            
        }
    }
    
}