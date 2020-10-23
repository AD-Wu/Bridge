package com.x.bridge.command.impl;

import com.x.bridge.command.core.ICommand;
import com.x.bridge.data.ChannelData;
import com.x.bridge.data.ReplierManager;
import com.x.bridge.proxy.ProxyManager;
import com.x.bridge.proxy.Replier;
import com.x.bridge.proxy.server.Proxy;

public class DisconnectCommand implements ICommand {
    
    @Override
    public void execute(ChannelData cd) {
        // 获取应用客户端地址
        String appSocket = cd.getAppSocketClient();
        // 获取代理
        Proxy proxy = ProxyManager.getProxy(cd.getProxyPort());
        // 获取应答管理者
        ReplierManager replierManager = proxy.getReplierManager();
        // 移除应答者
        Replier replier = replierManager.removeReplier(appSocket);
        // 关闭通道
        if (replier != null) {
            replier.close();
        }
    }
}
