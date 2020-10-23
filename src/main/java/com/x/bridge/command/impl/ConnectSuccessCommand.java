package com.x.bridge.command.impl;

import com.x.bridge.command.core.ICommand;
import com.x.bridge.data.ChannelData;
import com.x.bridge.data.ReplierManager;
import com.x.bridge.proxy.ProxyManager;
import com.x.bridge.proxy.Replier;
import com.x.bridge.proxy.server.Proxy;

public class ConnectSuccessCommand implements ICommand {
    
    @Override
    public void execute(ChannelData cd) {
        // 获取应用客户端地址
        String appSocket = cd.getAppSocketClient();
        // 获取代理
        Proxy proxy = ProxyManager.getProxy(cd.getProxyAddress());
        // 获取应答管理者
        ReplierManager replierManager = proxy.getReplierManager();
        // 获取应答者
        Replier replier = replierManager.getReplier(appSocket);
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
