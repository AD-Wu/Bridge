package com.x.bridge.proxy.core;

import com.x.bridge.command.core.Command;
import com.x.bridge.core.IService;
import com.x.bridge.core.SocketConfig;
import com.x.bridge.core.SocketServer;
import com.x.bridge.proxy.MessageType;
import com.x.bridge.proxy.data.ChannelData;
import com.x.bridge.proxy.data.ProxyConfig;
import com.x.bridge.proxy.server.ServerListener;
import com.x.bridge.util.AppHelper;
import com.x.doraemon.util.ArrayHelper;
import com.x.doraemon.util.Strings;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Desc TODO
 * @Date 2020/10/24 17:35
 * @Author AD
 */
@Log4j2
public class ProxyServer extends Proxy implements IService {
    
    private final ExecutorService runner;
    
    private final SocketServer server;
    
    public ProxyServer(ProxyConfig config, boolean serverModel) {
        super(config, true);
        this.runner = Executors.newSingleThreadExecutor();
        int port = AppHelper.getPort(config.getProxyAddress());
        this.server = new SocketServer(new SocketConfig(port), new ServerListener(replierManager));
    }
    
    @Override
    public void start() throws Exception {
        runner.execute(server);
    }
    
    @Override
    public void stop() throws Exception {
        server.stop();
    }
    
    public boolean connectRequest(Replier replier) {
        ChannelData cd = ChannelData.builder()
                .appSocketClient(replier.getAppSocketClient())
                .recvSeq(replier.getRecvSeq())
                .proxyAddress(replier.getChannelInfo().getLocalAddress())
                .targetAddress(config.getTargetAddress())
                .messageType(MessageType.ServerToClient.getCode())
                .command(Command.ConnectRequest.getCmd())
                .data(ArrayHelper.EMPTY_BYTE)
                .build();
        Object lock = replier.getConnectLock();
        try {
            synchronized (lock) {
                bridge.send(cd);
                lock.wait(config.getConnectTimeout() * 1000);
            }
            if (replier.isConnectTimeout()) {
                log.info("连接超时，配置时间:{}秒，连接关闭");
                return false;
            }
            return replier.isConnected();
        } catch (Exception e) {
            log.error(Strings.getExceptionTrace(e));
            return false;
        }
    }
    
}
