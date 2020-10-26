package com.x.bridge.proxy.command.impl;

import com.x.bridge.proxy.command.core.ICommand;
import com.x.bridge.proxy.ProxyManager;
import com.x.bridge.proxy.core.ProxyServer;
import com.x.bridge.proxy.core.Replier;
import com.x.bridge.proxy.data.ChannelData;

public class ConnectSuccess implements ICommand {
    
    @Override
    public void execute(ChannelData cd)throws Exception {
        // 获取应用客户端地址
        String appSocket = cd.getAppSocketClient();
        // 获取代理
        ProxyServer server = ProxyManager.getProxyServer(cd.getProxyName());
        // 获取应答者
        Replier replier = server.getReplier(appSocket);
        if (replier != null) {
            // 获取应答者连接建立锁
            Object connectLock = replier.getConnectLock();
            // 通知连接建立成功
            synchronized (connectLock) {
                replier.setConnected(true);
                replier.setConnectTimeout(false);
                connectLock.notify();
            }
        }
        
    }
    
}
