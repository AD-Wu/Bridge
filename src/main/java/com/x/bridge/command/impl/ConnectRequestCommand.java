package com.x.bridge.command.impl;

import com.x.bridge.command.core.ICommand;
import com.x.bridge.core.SocketClient;
import com.x.bridge.core.SocketConfig;
import com.x.bridge.proxy.BridgeManager;
import com.x.bridge.proxy.ProxyConfigManager;
import com.x.bridge.proxy.core.IBridge;
import com.x.bridge.proxy.data.ChannelData;
import com.x.bridge.proxy.ReplierManager;
import com.x.bridge.proxy.ProxyManager;
import com.x.bridge.proxy.core.Replier;
import com.x.bridge.proxy.client.ClientListener;
import com.x.bridge.proxy.core.Proxy;
import com.x.bridge.proxy.data.ProxyConfig;
import com.x.bridge.util.AppHelper;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ConnectRequestCommand implements ICommand {
    
    @Override
    public void execute(ChannelData cd) {
        // 获取应用客户端地址
        String appSocket = cd.getAppSocketClient();
        // 获取代理
        Proxy proxy = ProxyManager.getProxy(cd.getProxyName());
        // 首次请求
        if (proxy == null) {
            ProxyConfig config = ProxyConfigManager.getProxyConfig(cd.getProxyName());
            
            IBridge bridge = BridgeManager.getBridge(config.getBridge());
            proxy = new Proxy(cd.getProxyAddress(), bridge, false);
            ProxyManager.addProxy(cd.getProxyName(), proxy);
        }
        // 获取应答管理者
        ReplierManager replierManager = proxy.getReplierManager();
        // 获取应答者
        Replier replier = replierManager.getReplier(appSocket);
        // 未建立建立
        if (replier == null) {
            // 创建socket客户端连接目标服务器
            String ip = AppHelper.getIP(cd.getTargetAddress());
            int port = AppHelper.getPort(cd.getTargetAddress());
            SocketClient client = new SocketClient(
                    new SocketConfig(ip, port),
                    new ClientListener(appSocket, cd.getProxyAddress(), replierManager));
            proxy.getRunner().execute(client);
        } else {
            log.info("代理服务器:[{}],连接:[{}]已存在，不再重新建立",
                    cd.getProxyAddress(), cd.getAppSocketClient());
        }
    }
    
}
