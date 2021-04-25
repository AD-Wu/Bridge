package com.x.bridge.proxy.core;

import com.x.bridge.proxy.bridge.core.BridgeManager;
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
public abstract class Proxy {

    protected final ProxyConfig config;

    protected final boolean serverModel;

    protected final IBridge bridge;

    protected final Map<String, Replier> repliers;

    protected final Logger log = LogManager.getLogger(this.getClass());

    protected Proxy(ProxyConfig config, boolean serverModel) {
        this.config = config;
        this.serverModel = serverModel;
        this.bridge = BridgeManager.getBridge(config.getName());
        this.repliers = new ConcurrentHashMap<>();
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
        cd.setMessageTypeCode(type.getCode());
        cd.setCommandCode(Command.Disconnect.getCode());
        cd.setData(ArrayHelper.EMPTY_BYTE);
        try {
            bridge.send(cd);
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
        cd.setMessageTypeCode(type.getCode());
        cd.setCommandCode(Command.SendData.getCode());
        cd.setData(data);

        try {
            bridge.send(cd);
        } catch (Exception e) {
            log.error(StringHelper.getExceptionTrace(e));
        }
    }

    public void receive(ChannelData cd) {
        Command command = Command.get(cd.getCommandCode());
        if (command != null) {
            try {
                command.execute(cd);
            } catch (Exception e) {
                log.error(StringHelper.getExceptionTrace(e));
            }
        } else {
            log.error("代理收到非法指令:[{}]数据", cd.getCommandCode());
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
