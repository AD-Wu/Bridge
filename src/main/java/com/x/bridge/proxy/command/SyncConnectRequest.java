package com.x.bridge.proxy.command;

import com.x.bridge.common.SocketClient;
import com.x.bridge.common.SocketConfig;
import com.x.bridge.data.ChannelData;
import com.x.bridge.proxy.client.ProxyClientListener;
import com.x.bridge.proxy.command.core.ICommand;
import com.x.bridge.proxy.core.Command;
import com.x.bridge.proxy.core.Proxy;
import com.x.bridge.proxy.core.Replier;
import com.x.bridge.util.ProxyHelper;
import com.x.doraemon.util.ArrayHelper;
import lombok.extern.log4j.Log4j2;

/**
 * @Desc
 * @Date 2021/5/3 19:19
 * @Author AD
 */
@Log4j2
public class SyncConnectRequest implements ICommand<ChannelData>{

    @Override
    public void receive(Proxy<ChannelData> proxy, ChannelData cd) {
        // 获取应用客户端地址
        String appSocket = cd.getAppClient();
        // 获取应答者
        Replier replier = proxy.getReplier(appSocket);
        // 未建立连接
        if (replier == null) {
            // 创建socket客户端连接目标服务器
            String ip = ProxyHelper.getIP(cd.getAppServer());
            int port = ProxyHelper.getPort(cd.getAppServer());
            SocketClient socket = new SocketClient(
                    proxy.name(),
                    SocketConfig.getClientConfig(ip, port),
                    new ProxyClientListener(appSocket, cd.getProxyServer(), proxy));
            socket.start();
        } else {
            log.info("代理服务器:[{}],连接:[{}]已存在，不再重新建立",
                    cd.getProxyServer(), cd.getAppClient());
        }
    }

    public static ChannelData getChannelData(Replier replier) {
        ChannelData cd = ChannelData.generate(replier);
        cd.setCommand(Command.SyncConnectRequest);
        cd.setData(ArrayHelper.EMPTY_BYTE);
        return cd;
    }

}
