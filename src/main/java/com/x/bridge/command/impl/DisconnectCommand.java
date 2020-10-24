package com.x.bridge.command.impl;

import com.x.bridge.command.core.ICommand;
import com.x.bridge.proxy.data.ChannelData;
import com.x.bridge.proxy.ReplierManager;
import com.x.bridge.proxy.ProxyManager;
import com.x.bridge.proxy.core.Replier;
import com.x.bridge.proxy.core.Proxy;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class DisconnectCommand implements ICommand {
    
    @Override
    public void execute(ChannelData cd) {
        // 获取应用客户端地址
        String appSocket = cd.getAppSocketClient();
        // 获取代理
        Proxy proxy = ProxyManager.getProxy(cd.getProxyName());
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
