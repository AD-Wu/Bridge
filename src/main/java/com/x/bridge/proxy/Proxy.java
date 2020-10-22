package com.x.bridge.proxy;

import com.x.bridge.command.Command;
import com.x.bridge.core.SocketConfig;
import com.x.bridge.core.SocketServer;
import com.x.bridge.data.ChannelData;
import com.x.bridge.data.IBridge;
import com.x.bridge.data.ProxyConfig;
import com.x.bridge.data.ReplierManager;
import com.x.doraemon.util.ArrayHelper;
import com.x.doraemon.util.Strings;
import lombok.extern.log4j.Log4j2;

/**
 * @Desc TODO
 * @Date 2020/10/22 01:07
 * @Author AD
 */
@Log4j2
public class Proxy {
    
    private final SocketServer server;
    
    private final ReplierManager replierManager;
    
    private final ProxyConfig config;
    
    private final IBridge bridge;
    
    public Proxy(int serverPort, IBridge bridge) {
        this.bridge = bridge;
        this.config = ProxyConfigManager.getProxyConfig(serverPort);
        this.replierManager = new ReplierManager(serverPort);
        this.server = new SocketServer(SocketConfig.serverConfig(serverPort),
                new ProxyServerListener(replierManager));
    }
    
    boolean connectRequest(Replier replier) {
        ChannelData cd = new ChannelData();
        cd.setChannelInfo(replier.getChannelInfo());
        cd.setRecvSeq(replier.getRecvSeq());
        cd.setCmd(Command.ConnectRequest);
        cd.setData(ArrayHelper.EMPTY_BYTE);
        
        Object lock = replier.getConnectLock();
        try {
            synchronized (lock) {
                bridge.send(cd);
                lock.wait(config.getConnectTimeout() * 1000);
            }
            return replier.isConnected();
        } catch (Exception e) {
            log.error(Strings.getExceptionTrace(e));
            return false;
        }
    }
    
    void disconnect(Replier replier) {
        ChannelData cd = new ChannelData();
        cd.setChannelInfo(replier.getChannelInfo());
        cd.setRecvSeq(replier.getRecvSeq());
        cd.setCmd(Command.Disconnect);
        cd.setData(ArrayHelper.EMPTY_BYTE);
        try {
            bridge.send(cd);
        } catch (Exception e) {
            log.error(Strings.getExceptionTrace(e));
        }
    }
    
    void receive(Replier replier, byte[] data) {
        ChannelData cd = new ChannelData();
        cd.setChannelInfo(replier.getChannelInfo());
        cd.setRecvSeq(replier.getRecvSeq());
        cd.setCmd(Command.SendData);
        cd.setData(data);
        try {
            bridge.send(cd);
        } catch (Exception e) {
            log.error(Strings.getExceptionTrace(e));
        }
    }
    
}
