package com.x.bridge.proxy.core;

import com.x.bridge.common.IReceiver;
import com.x.bridge.common.ISender;
import com.x.bridge.common.IService;
import com.x.bridge.data.ChannelInfo;
import com.x.bridge.data.ProxyConfig;
import com.x.bridge.util.ProxyHelper;
import com.x.doraemon.util.StringHelper;
import io.netty.channel.ChannelHandlerContext;
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
    
    protected volatile boolean running;
    protected final ProxyConfig config;
    protected final ISender<T> sender;
    protected final Map<String, Replier> repliers;
    
    protected Proxy(ProxyConfig config, ISender<T> sender) {
        this.repliers = new ConcurrentHashMap<>();
        this.config = config;
        this.sender = sender;
        sender.setReceiver(this);
    }
    
    public void send(T data) {
        if (running) {
            try {
                sender.send(data);
                log.info("发送数据:{}", data);
            } catch (Exception e) {
                log.error("发送数据异常:{}", StringHelper.getExceptionTrace(e));
            }
        } else {
            log.error("代理未运行");
        }
    }
    
    public void addReplier(String appClient, Replier replier) {
        repliers.put(appClient, replier);
    }
    
    public Replier getReplier(String appClient) {
        return repliers.get(appClient);
    }
    
    public Replier removeReplier(String appClient) {
        return repliers.remove(appClient);
    }
    
    public boolean isAccept(ChannelHandlerContext ctx) {
        ChannelInfo chn = ProxyHelper.getChannelInfo(ctx);
        return config.getAllowClients().contains(chn.getRemoteAddress());
    }
    
    public ProxyConfig getConfig() {
        return config;
    }
    
    public void setRunning(boolean running){
        this.running = running;
    }
    
    
}
