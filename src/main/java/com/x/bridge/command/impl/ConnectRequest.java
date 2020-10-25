package com.x.bridge.command.impl;

import com.x.bridge.command.core.ICommand;
import com.x.bridge.core.SocketClient;
import com.x.bridge.core.SocketConfig;
import com.x.bridge.proxy.ProxyConfigs;
import com.x.bridge.proxy.ProxyManager;
import com.x.bridge.proxy.core.ClientListener;
import com.x.bridge.proxy.core.ProxyClient;
import com.x.bridge.proxy.core.Replier;
import com.x.bridge.proxy.data.ChannelData;
import com.x.bridge.proxy.data.ProxyConfig;
import com.x.bridge.util.AppHelper;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ConnectRequest implements ICommand {
    
    @Override
    public void execute(ChannelData cd) throws Exception{
        // 获取应用客户端地址
        String appSocket = cd.getAppSocketClient();
        // 获取代理
        ProxyClient client = ProxyManager.getProxyClient(cd.getProxyName());
        // 首次请求
        if (client == null) {
            ProxyConfig config = ProxyConfigs.get(cd.getProxyName());
            client = new ProxyClient(config);
            ProxyManager.addProxyClient(cd.getProxyName(), client);
        }
        // 获取应答者
        Replier replier = client.getReplier(appSocket);
        // 未建立建立
        if (replier == null) {
            // 创建socket客户端连接目标服务器
            String ip = AppHelper.getIP(cd.getTargetAddress());
            int port = AppHelper.getPort(cd.getTargetAddress());
            SocketClient socket = new SocketClient(
                    new SocketConfig(ip, port),
                    new ClientListener(appSocket, cd.getProxyAddress(), client));
            client.getRunner().execute(socket);
        } else {
            log.info("代理服务器:[{}],连接:[{}]已存在，不再重新建立",
                    cd.getProxyAddress(), cd.getAppSocketClient());
        }
    }
    
}
