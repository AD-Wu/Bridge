package com.x.bridge.proxy.server;

import com.x.bridge.common.SocketConfig;
import com.x.bridge.common.SocketServer;
import com.x.bridge.proxy.core.Command;
import com.x.bridge.proxy.core.Proxy;
import com.x.bridge.proxy.core.Replier;
import com.x.bridge.data.ChannelData;
import com.x.bridge.proxy.core.MessageType;
import com.x.bridge.data.ProxyConfig;
import com.x.bridge.util.ProxyHelper;
import com.x.doraemon.util.ArrayHelper;
import com.x.doraemon.util.StringHelper;
import lombok.extern.log4j.Log4j2;

/**
 * @Desc 代理服务端
 * @Date 2020/10/24 17:35
 * @Author AD
 */
@Log4j2
public class ProxyServer extends Proxy {
    
    private final SocketServer server;
    
    public ProxyServer(ProxyConfig config) {
        super(config, true, null);
        int port = ProxyHelper.getPort(config.getProxyServer());
        this.server = new SocketServer(new SocketConfig(port), new ProxyServerListener(this));
    }
    
    @Override
    public void start() throws Exception {
        server.start();
    }
    
    @Override
    public void stop() throws Exception {
        server.stop();
        repliers.clear();
    }
    
    public boolean isAccept(String socket) {
        return config.isAllowClient(socket);
    }
    
    public boolean connectRequest(Replier replier) {
        ChannelData cd = ChannelData.generate(config.getName(), replier, MessageType.ServerToClient);
        cd.setCommand(Command.ConnectRequest);
        cd.setData(ArrayHelper.EMPTY_BYTE);
        Object lock = replier.getConnectLock();
        try {
            synchronized (lock) {
                sender.send(cd);
                lock.wait(config.getConnectTimeout() * 1000);
            }
            if (replier.isConnectTimeout()) {
                log.info("连接超时，配置时间:[{}]秒，连接关闭", config.getConnectTimeout());
                return false;
            }
            return replier.isConnected();
        } catch (Exception e) {
            log.error(StringHelper.getExceptionTrace(e));
            return false;
        }
    }
    
}
