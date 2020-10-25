package com.x.bridge.command.impl;

import com.x.bridge.command.core.ICommand;
import com.x.bridge.proxy.ProxyManager;
import com.x.bridge.proxy.core.Proxy;
import com.x.bridge.proxy.core.Replier;
import com.x.bridge.proxy.data.ChannelData;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Disconnect implements ICommand {
    
    @Override
    public void execute(ChannelData cd) throws Exception {
        // 获取应用客户端地址
        String appSocket = cd.getAppSocketClient();
        
        // 获取代理
        Proxy proxy = null;
        String sc = "";
        switch (cd.getMessageType()) {
            case ServerToClient:
                proxy = ProxyManager.getProxyClient(cd.getProxyName());
                sc = "客户端";
                break;
            case ClientToServer:
                proxy = ProxyManager.getProxyServer(cd.getProxyName());
                sc = "服务端";
                break;
            default:
                throw new RuntimeException("消息类型错误，当前消息类型代码:" + cd.getMessageType());
        }
        // 移除应答者
        Replier replier = proxy.removeReplier(appSocket);
        // 关闭通道
        if (replier != null) {
            // 日志记录
            log.info("连接关闭，客户端:[{}]，代理({}):[{}]，服务端:[{}]",
                    appSocket, sc, replier.getChannelInfo().getLocalAddress(),
                    replier.getChannelInfo().getRemoteAddress());
            replier.setConnected(false);
            replier.close();
        }
    }
    
}
