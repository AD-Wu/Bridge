package com.x.bridge.command.impl;

import com.x.bridge.command.core.ICommand;
import com.x.bridge.core.SocketClient;
import com.x.bridge.core.SocketConfig;
import com.x.bridge.data.ChannelData;
import com.x.bridge.data.ReplierManager;
import com.x.bridge.proxy.ProxyManager;
import com.x.bridge.proxy.Replier;
import com.x.bridge.proxy.client.ClientListener;
import com.x.bridge.proxy.server.Proxy;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ConnectRequestCommand implements ICommand {
    
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
        // 未建立建立
        if (replier == null) {
            // 创建socket客户端连接目标服务器
            SocketClient client = new SocketClient(
                    new SocketConfig(cd.getTargetIP(), cd.getTargetPort()),
                    new ClientListener(appSocket, replierManager));
            proxy.getRunner().execute(client);
        } else {
            log.info("代理服务器:[{}],连接:[{}]已存在，不再重新建立",
                    cd.getProxyAddress(), cd.getAppSocketClient());
        }
    }
}
