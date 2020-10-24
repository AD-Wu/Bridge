package com.x.bridge.proxy.core;

import com.x.bridge.command.core.Command;
import com.x.bridge.command.core.Commands;
import com.x.bridge.command.core.ICommand;
import com.x.bridge.core.IService;
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
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Desc 代理对象
 * @Date 2020/10/22 01:07
 * @Author AD
 */
@Log4j2
public class Proxy implements IService {
    
    @Getter
    private final IBridge bridge;
    
    @Getter
    private final boolean serverModel;
    
    @Getter
    private final ExecutorService runner;
    
    @Getter
    private final ProxyConfig config;
    
    @Getter
    private final ReplierManager replierManager;
    
    private final SocketServer server;
    
    public Proxy(String proxyAddress, IBridge bridge, boolean serverModel) {
        this.bridge = bridge;
        this.serverModel = serverModel;
        this.runner = Executors.newCachedThreadPool();
        this.config = ProxyConfigManager.getProxyConfig(proxyAddress);
        this.replierManager = new ReplierManager(proxyAddress);
        if (serverModel) {
            this.server = new SocketServer(new SocketConfig(AppHelper.getPort(proxyAddress)),
                    new ServerListener(replierManager));
        } else {
            server = null;
        }
        
    }
    
    @Override
    public void start() throws Exception{
        if(serverModel){
            runner.execute(server);
        }
        runner.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    bridge.start();
                } catch (Exception e) {
                    log.error(Strings.getExceptionTrace(e));
                }
            }
        });
    }
    
    @Override
    public void stop() throws Exception{
        if(serverModel){
            server.stop();
        }
        runner.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    bridge.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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
