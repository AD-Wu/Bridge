package com.x.bridge.proxy.command.impl;

import com.x.bridge.proxy.command.core.ICommand;
import com.x.bridge.proxy.data.MessageType;
import com.x.bridge.proxy.ProxyManager;
import com.x.bridge.proxy.core.Proxy;
import com.x.bridge.proxy.core.Replier;
import com.x.bridge.proxy.data.ChannelData;

public class SendData implements ICommand {
    
    @Override
    public void execute(ChannelData cd) {
        // 获取应用客户端地址
        String appSocket = cd.getAppSocketClient();
        // 获取消息类型
        int messageType = cd.getMessageType().getCode();
        // 获取代理
        Proxy proxy = null;
        if (MessageType.ClientToServer.getCode() == messageType) {
            proxy = ProxyManager.getProxyServer(cd.getProxyName());
        } else if (MessageType.ServerToClient.getCode() == messageType) {
            proxy = ProxyManager.getProxyClient(cd.getProxyName());
        } else {
            throw new RuntimeException("消息类型错误，当前消息类型代码:" + messageType);
        }
        // 获取应答者
        Replier replier = proxy.getReplier(appSocket);
        if (replier != null) {
            // 获取发送数据
            replier.send(cd.getRecvSeq(),cd.getData());
        }
    }
    
}
