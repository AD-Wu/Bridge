package com.x.bridge.proxy.core;

import com.x.bridge.command.core.Command;
import com.x.bridge.command.core.Commands;
import com.x.bridge.command.core.ICommand;
import com.x.bridge.core.IService;
import com.x.bridge.proxy.bridge.BridgeManager;
import com.x.bridge.proxy.bridge.IBridge;
import com.x.bridge.proxy.data.ChannelData;
import com.x.bridge.proxy.data.MessageType;
import com.x.bridge.proxy.data.ProxyConfig;
import com.x.doraemon.util.ArrayHelper;
import com.x.doraemon.util.Strings;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Desc 代理对象，服务端即socket server，客户端即socket会话管理
 * @Date 2020/10/24 18:00
 * @Author AD
 */
@Data
public class Proxy implements IService {
    
    protected final ProxyConfig config;
    
    protected final boolean serverModel;
    
    protected final IBridge bridge;
    
    protected final Map<String, Replier> repliers;
    
    protected final ExecutorService runner;
    
    protected final Logger log = LogManager.getLogger(this.getClass());
    
    protected Proxy(ProxyConfig config, boolean serverModel) {
        this.config = config;
        this.serverModel = serverModel;
        this.bridge = BridgeManager.getBridge(config.getName());
        this.repliers = new ConcurrentHashMap<>();
        this.runner = Executors.newCachedThreadPool();
    }
    
    public void addReplier(String remoteAddress, Replier replier) {
        repliers.put(remoteAddress, replier);
    }
    
    public Replier getReplier(String remoteAddress) {
        return repliers.get(remoteAddress);
    }
    
    public Replier removeReplier(String remoteAddress) {
        return repliers.remove(remoteAddress);
    }
    
    public void disconnect(Replier replier, MessageType type) {
        String target = MessageType.ClientToServer == type ?
                replier.getChannelInfo().getRemoteAddress() :
                config.getTargetAddress();
        ChannelData cd = ChannelData.builder()
                .proxyName(config.getName())
                .appSocketClient(replier.getAppSocketClient())
                .recvSeq(replier.getRecvSeq())
                .proxyAddress(replier.getProxyAddress())
                .targetAddress(target)
                .messageType(type)
                .command(Command.Disconnect)
                .data(ArrayHelper.EMPTY_BYTE)
                .build();
        try {
            bridge.send(cd);
        } catch (Exception e) {
            log.error(Strings.getExceptionTrace(e));
        }
    }
    
    public void send(Replier replier, MessageType type, byte[] data) {
        String target = MessageType.ClientToServer == type ?
                replier.getChannelInfo().getRemoteAddress() :
                config.getTargetAddress();
        String proxyAddress = MessageType.ClientToServer == type ?
                replier.getChannelInfo().getLocalAddress() :
                config.getTargetAddress();
        ChannelData cd = ChannelData.builder()
                .proxyName(config.getName())
                .appSocketClient(replier.getAppSocketClient())
                .recvSeq(replier.getRecvSeq())
                .proxyAddress(proxyAddress)
                .targetAddress(target)
                .messageType(type)
                .command(Command.SendData)
                .data(data)
                .build();
        try {
            bridge.send(cd);
        } catch (Exception e) {
            log.error(Strings.getExceptionTrace(e));
        }
    }
    
    public void receive(ChannelData cd) {
        ICommand command = Commands.getCommand(cd.getCommand().getCmd());
        if (command != null) {
            try {
                command.execute(cd);
            } catch (Exception e) {
                log.error(Strings.getExceptionTrace(e));
            }
        } else {
            log.error("代理收到非法指令:[{}]数据", cd.getCommand());
        }
    }
    
    @Override
    public void start() throws Exception {
        runner.execute(() -> {
            try {
                bridge.start();
                proxyStart();
            } catch (Exception e) {
                try {
                    stop();
                } catch (Exception exception) {
                    log.error(Strings.getExceptionTrace(e));
                }
                log.error(Strings.getExceptionTrace(e));
            }
        });
    }
    
    @Override
    public void stop() throws Exception {
        runner.execute(() -> {
            try {
                bridge.stop();
                proxyStop();
            } catch (Exception e) {
                log.error(Strings.getExceptionTrace(e));
            }
        });
    }
    
    protected void proxyStart() throws Exception {}
    
    protected void proxyStop() throws Exception  {}
    
}
