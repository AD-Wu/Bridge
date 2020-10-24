package com.x.bridge.command.impl;

import com.x.bridge.command.core.ICommand;
import com.x.bridge.proxy.data.ChannelData;
import com.x.bridge.proxy.ReplierManager;
import com.x.bridge.proxy.ProxyManager;
import com.x.bridge.proxy.core.Replier;
import com.x.bridge.proxy.core.Proxy;

public class SendDataCommand implements ICommand {
    
    @Override
    public void execute(ChannelData cd) {
        // 获取应用客户端地址
        String appSocket = cd.getAppSocketClient();
        // 获取代理
        Proxy proxy = ProxyManager.getProxy(cd.getProxyName());
        // 获取应答管理者
        ReplierManager replierManager = proxy.getReplierManager();
        // 获取应答者
        Replier replier = replierManager.getReplier(appSocket);
        if (replier != null) {
            // 获取发送数据
            replier.send(cd.getRecvSeq(),cd.getData());
        }
    }
    
}
