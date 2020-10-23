package com.x.bridge.command.impl;

import com.x.bridge.command.core.ICommand;
import com.x.bridge.core.SocketClient;
import com.x.bridge.core.SocketConfig;
import com.x.bridge.data.ChannelData;
import com.x.bridge.data.ReplierManager;
import com.x.bridge.proxy.ProxyManager;
import com.x.bridge.proxy.Replier;
import com.x.bridge.proxy.client.ProxyClientListener;
import com.x.bridge.proxy.server.Proxy;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ConnectRequestCommand implements ICommand {


    @Override
    public void execute(ChannelData cd) {
        String remote = cd.getRemoteAddress();
        Proxy proxy = ProxyManager.getProxyServer(cd.getProxyPort());
        ReplierManager replierManager = proxy.getReplierManager();
        Replier replier = replierManager.getReplier(remote);
        if (replier == null) {
            SocketConfig config = SocketConfig.clientConfig(cd.getTargetIp(), cd.getTargetPort());
            SocketClient client = new SocketClient(config, new ProxyClientListener(remote, replierManager));
            client.connect();
        } else {
            log.info("代理服务器:{},连接:{}已存在，不再重新建立", cd.getProxyPort(), cd.getRemoteAddress());
        }
    }
}
