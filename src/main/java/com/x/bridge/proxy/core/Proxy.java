package com.x.bridge.proxy.core;

import com.x.bridge.proxy.bridge.core.IReceiver;
import com.x.bridge.proxy.bridge.core.IBridge;
import com.x.bridge.proxy.data.ChannelData;
import com.x.bridge.proxy.data.MessageType;
import com.x.bridge.proxy.data.ProxyConfig;
import com.x.doraemon.util.ArrayHelper;
import com.x.doraemon.util.StringHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Desc 代理对象，服务端即socket server，客户端即socket会话管理
 * @Date 2020/10/24 18:00
 * @Author AD
 */
// @Data
public abstract class Proxy implements IReceiver {
    
    protected final ProxyConfig config;
    protected final boolean serverModel;
    protected final IBridge sender;
    protected final Map<String, Replier> repliers;
    protected final Logger log = LogManager.getLogger(this.getClass());
    
    protected Proxy(ProxyConfig config, boolean serverModel, IBridge sender) {
        this.repliers = new ConcurrentHashMap<>();
        this.config = config;
        this.serverModel = serverModel;
        this.sender = sender;
        sender.addReceiver(this);
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
        
        ChannelData cd = new ChannelData();
        cd.setProxyName(config.getName());
        cd.setAppSocketClient(replier.getAppSocketClient());
        cd.setSeq(replier.getRecvSeq());
        cd.setProxyAddress(replier.getProxyAddress());
        cd.setTargetAddress(target);
        cd.setMessageType(type);
        cd.setCommand(Command.Disconnect);
        cd.setData(ArrayHelper.EMPTY_BYTE);
        try {
            sender.send(cd);
        } catch (Exception e) {
            log.error(StringHelper.getExceptionTrace(e));
        }
    }
    
    public void send(Replier replier, MessageType type, byte[] data) {
        String target = MessageType.ClientToServer == type ?
                replier.getChannelInfo().getRemoteAddress() :
                config.getTargetAddress();
        String proxyAddress = MessageType.ClientToServer == type ?
                replier.getChannelInfo().getLocalAddress() :
                config.getTargetAddress();
        
        ChannelData cd = new ChannelData();
        cd.setProxyName(config.getName());
        cd.setAppSocketClient(replier.getAppSocketClient());
        cd.setSeq(replier.getRecvSeq());
        cd.setProxyAddress(proxyAddress);
        cd.setTargetAddress(target);
        cd.setMessageType(type);
        cd.setCommand(Command.SendData);
        cd.setData(data);
        
        try {
            sender.send(cd);
        } catch (Exception e) {
            log.error(StringHelper.getExceptionTrace(e));
        }
    }
    
   
    
    @Override
    public final void receive(ChannelData... datas) throws Exception {
        if (datas != null && datas.length > 0) {
            for (ChannelData data : datas) {
                Command command = data.getCommand();
                if (command != null) {
                    try {
                        command.execute(this, data);
                    } catch (Exception e) {
                        log.error(StringHelper.getExceptionTrace(e));
                    }
                } else {
                    log.error("代理收到非法指令:[{}]数据", data.getCommand());
                }
            }
        }
    }
    
    public ProxyConfig getConfig() {
        return config;
    }
    
    public boolean isServerModel() {
        return serverModel;
    }
    
    public abstract void start() throws Exception;
    
    public abstract void stop() throws Exception;
    
}
