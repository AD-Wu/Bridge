package com.x.bridge.proxy.server;

import com.x.bridge.common.ISender;
import com.x.bridge.common.SocketConfig;
import com.x.bridge.common.SocketServer;
import com.x.bridge.data.ChannelData;
import com.x.bridge.data.ProxyConfig;
import com.x.bridge.proxy.command.core.ICommand;
import com.x.bridge.proxy.core.Command;
import com.x.bridge.proxy.core.Proxy;
import com.x.bridge.util.ProxyHelper;
import com.x.doraemon.util.ArrayHelper;
import lombok.extern.log4j.Log4j2;

/**
 * @Desc 代理服务端
 * @Date 2020/10/24 17:35
 * @Author AD
 */
@Log4j2
public class ProxyServer extends Proxy<ChannelData> {
    
    private final SocketServer server;
    
    public ProxyServer(ProxyConfig config, ISender<ChannelData> sender) {
        super(config, sender);
        int port = ProxyHelper.getPort(config.getProxyServer());
        ProxyServerListener listener = new ProxyServerListener(this);
        this.server = new SocketServer(config.getName(), SocketConfig.getServerConfig(port), listener);
    }
    
    @Override
    public String name() {
        return config.getName();
    }
    
    @Override
    public void start() throws Exception {
        server.start();
        sender.start();
    }
    
    @Override
    public void stop() throws Exception {
        server.stop();
        sender.stop();
        repliers.clear();
    }
    
    
    @Override
    public void onReceive(ChannelData... datas) {
        if (!ArrayHelper.isEmpty(datas)) {
            for (ChannelData data : datas) {
                Command command = data.getCommand();
                if (command != null) {
                    try {
                        ICommand<ChannelData> actor = command.getActor();
                        actor.execute(this, data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    
}
