package com.x.bridge.proxy.core;

import com.x.bridge.command.core.Command;
import com.x.bridge.command.core.Commands;
import com.x.bridge.command.core.ICommand;
import com.x.bridge.core.SocketConfig;
import com.x.bridge.core.SocketServer;
import com.x.bridge.proxy.ProxyConfigManager;
import com.x.bridge.proxy.ReplierManager;
import com.x.bridge.proxy.data.ChannelData;
import com.x.bridge.proxy.data.ProxyConfig;
import com.x.bridge.proxy.server.ServerListener;
import com.x.bridge.util.AppHelper;
import com.x.doraemon.util.ArrayHelper;
import com.x.doraemon.util.Strings;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Desc 代理对象
 * @Date 2020/10/22 01:07
 * @Author AD
 */
@Log4j2
@Data
public class Proxy {
    
    private final SocketServer server;
    
    private final ReplierManager replierManager;
    
    private final ProxyConfig config;
    
    private final IBridge bridge;
    
    private final ExecutorService runner;
    
    public Proxy(String proxyAddress, IBridge bridge) {
        this.bridge = bridge;
        this.runner = Executors.newCachedThreadPool();
        this.config = ProxyConfigManager.getProxyConfig(proxyAddress);
        this.replierManager = new ReplierManager(proxyAddress);
        this.server = new SocketServer(new SocketConfig(AppHelper.getPort(proxyAddress)),
                new ServerListener(replierManager));
    }
    
    public boolean connectRequest(Replier replier) {
        ChannelData cd = ChannelData.builder()
                .appSocketClient(replier.getAppSocketClient())
                .recvSeq(replier.getRecvSeq())
                .proxyAddress(replier.getChannelInfo().getLocalAddress())
                .targetAddress(config.getTargetAddress())
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
    
    public void connectSuccess(Replier replier) {
        ChannelData cd = ChannelData.builder()
                .appSocketClient(replier.getAppSocketClient())
                .recvSeq(replier.getRecvSeq())
                .targetAddress(replier.getChannelInfo().getRemoteAddress())
                .command(Command.ConnectSuccess.getCmd())
                .data(ArrayHelper.EMPTY_BYTE)
                .build();
        try {
            bridge.send(cd);
        } catch (Exception e) {
            log.error(Strings.getExceptionTrace(e));
        }
    }
    
    public void disconnect(Replier replier) {
        ChannelData cd = ChannelData.builder()
                .appSocketClient(replier.getAppSocketClient())
                .recvSeq(replier.getRecvSeq())
                .proxyAddress(replier.getProxyAddress())
                .targetAddress(config.getTargetAddress())
                .command(Command.Disconnect.getCmd())
                .data(ArrayHelper.EMPTY_BYTE)
                .build();
        try {
            bridge.send(cd);
        } catch (Exception e) {
            log.error(Strings.getExceptionTrace(e));
        }
    }
    
    public void sendToProxy(Replier replier, byte[] data) {
        ChannelData cd = ChannelData.builder()
                .appSocketClient(replier.getAppSocketClient())
                .recvSeq(replier.getRecvSeq())
                .proxyAddress(replier.getProxyAddress())
                .targetAddress(config.getTargetAddress())
                .command(Command.SendData.getCmd())
                .data(data)
                .build();
        try {
            bridge.send(cd);
        } catch (Exception e) {
            log.error(Strings.getExceptionTrace(e));
        }
    }
    
    public void recvFromProxy(ChannelData cd) {
        ICommand command = Commands.getCommand(cd.getCommand());
        if (command != null) {
            command.execute(cd);
        } else {
            log.info("代理收到非法指令:[{}]数据", cd.getCommand());
        }
        
    }
    
}
