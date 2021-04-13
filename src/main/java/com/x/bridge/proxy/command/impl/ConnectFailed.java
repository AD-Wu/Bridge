package com.x.bridge.proxy.command.impl;

import com.x.bridge.proxy.command.core.ICommand;
import com.x.bridge.proxy.ProxyManager;
import com.x.bridge.proxy.core.ProxyServer;
import com.x.bridge.proxy.core.Replier;
import com.x.bridge.proxy.data.ChannelData;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ConnectFailed implements ICommand {
    
    @Override
    public void execute(ChannelData cd) throws Exception{
        // 获取应用客户端地址
        String appSocket = cd.getAppSocketClient();
        // 获取代理
        ProxyServer server = ProxyManager.getProxyServer(cd.getProxyName());
        // 移除应答者
        Replier replier = server.removeReplier(appSocket);
        if (replier != null) {
            // 获取应答者连接建立锁
            Object connectLock = replier.getConnectLock();
            // 通知连接建立成功
            synchronized (connectLock) {
                replier.setConnected(false);
                connectLock.notify();
            }
        }
        log.info("应用客户端:[{}]与目标服务器:[{}]连接建立失败", cd.getAppSocketClient(), cd.getTargetAddress());
    }
    
}
