package com.x.bridge.proxy.core;

import com.x.bridge.command.core.Command;
import com.x.bridge.command.core.Commands;
import com.x.bridge.command.core.ICommand;
import com.x.bridge.proxy.BridgeManager;
import com.x.bridge.proxy.MessageType;
import com.x.bridge.proxy.ReplierManager;
import com.x.bridge.proxy.data.ChannelData;
import com.x.bridge.proxy.data.ProxyConfig;
import com.x.doraemon.util.ArrayHelper;
import com.x.doraemon.util.Strings;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @Desc TODO
 * @Date 2020/10/24 18:00
 * @Author AD
 */
@Data
public class Proxy {
    
    protected final ProxyConfig config;
    
    protected final boolean serverModel;
    
    protected final IBridge bridge;
    
    protected final ReplierManager replierManager;
    
    protected final Logger log = LogManager.getLogger(this.getClass());
    
    protected Proxy(ProxyConfig config, boolean serverModel) {
        this.config = config;
        this.serverModel = serverModel;
        this.bridge = BridgeManager.getBridge(config.getBridge());
        this.replierManager = new ReplierManager(config.getName());
    }
    
    public void disconnect(Replier replier,MessageType type) {
        ChannelData cd = ChannelData.builder()
                .appSocketClient(replier.getAppSocketClient())
                .recvSeq(replier.getRecvSeq())
                .proxyAddress(replier.getProxyAddress())
                .targetAddress(config.getTargetAddress())
                .messageType(type.getCode())
                .command(Command.Disconnect.getCmd())
                .data(ArrayHelper.EMPTY_BYTE)
                .build();
        try {
            bridge.send(cd);
        } catch (Exception e) {
            log.error(Strings.getExceptionTrace(e));
        }
    }
    
    public void send(Replier replier, MessageType type, byte[] data) {
        ChannelData cd = ChannelData.builder()
                .appSocketClient(replier.getAppSocketClient())
                .recvSeq(replier.getRecvSeq())
                .proxyAddress(replier.getProxyAddress())
                .targetAddress(config.getTargetAddress())
                .messageType(type.getCode())
                .command(Command.SendData.getCmd())
                .data(data)
                .build();
        try {
            bridge.send(cd);
        } catch (Exception e) {
            log.error(Strings.getExceptionTrace(e));
        }
    }
    
    public void receive(ChannelData cd) {
        ICommand command = Commands.getCommand(cd.getCommand());
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
    
    public ProxyConfig getConfig() {
        return config;
    }
    
    public IBridge getBridge() {
        return bridge;
    }
    
    public ReplierManager getReplierManager() {
        return replierManager;
    }
    
}
