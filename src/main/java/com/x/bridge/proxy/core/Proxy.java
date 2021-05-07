package com.x.bridge.proxy.core;

import com.x.bridge.common.IReceiver;
import com.x.bridge.common.ISender;
import com.x.bridge.common.IService;
import com.x.bridge.data.ProxyConfig;
import lombok.extern.log4j.Log4j2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Desc 代理对象，服务端即socket server，客户端即socket会话管理
 * @Date 2020/10/24 18:00
 * @Author AD
 */
@Log4j2
public abstract class Proxy<T> implements IService, IReceiver<T> {

    protected volatile boolean running = false;
    protected final ProxyConfig config;
    protected final ISender<T> sender;
    protected final Map<String, Replier> repliers;

    protected Proxy(ProxyConfig config, ISender<T> sender) {
        this.repliers = new ConcurrentHashMap<>();
        this.config = config;
        this.sender = sender;
        sender.setReceiver(this);
    }

    // public void send(Replier replier, MessageType type, byte[] data) {
    //     ChannelData cd = ChannelData.generate(config.getName(), replier, type);
    //     cd.setCommand(Command.SendData);
    //     cd.setData(data);
    //     try {
    //         sender.send(cd);
    //     } catch (Exception e) {
    //         log.error(StringHelper.getExceptionTrace(e));
    //     }
    // }

    public void send(T data) throws Exception {
        if (running) {
            sender.send(data);
        } else {
            throw new RuntimeException("代理未运行");
        }

    }

    // public void disconnect(Replier replier, MessageType type) {
    //     ChannelData data = ChannelData.generate(config.getName(), replier, type);
    //     data.setCommand(Command.Disconnect);
    //     data.setData(ArrayHelper.EMPTY_BYTE);
    //     try {
    //         sender.send(data);
    //     } catch (Exception e) {
    //         log.error(StringHelper.getExceptionTrace(e));
    //     }
    // }

    public void addReplier(String appSocketClient, Replier replier) {
        repliers.put(appSocketClient, replier);
    }

    public Replier getReplier(String appSocketClient) {
        return repliers.get(appSocketClient);
    }

    public Replier removeReplier(String appSocketClient) {
        return repliers.remove(appSocketClient);
    }

    public ProxyConfig getConfig() {
        return config;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

}
