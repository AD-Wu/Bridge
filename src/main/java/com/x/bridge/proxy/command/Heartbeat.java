package com.x.bridge.proxy.command;

import com.google.common.cache.*;
import com.x.bridge.data.ChannelData;
import com.x.bridge.proxy.command.core.ICommand;
import com.x.bridge.proxy.core.Proxy;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.TimeUnit;

/**
 * @Desc
 * @Date 2021/5/6 19:52
 * @Author AD
 */
@Log4j2
public class Heartbeat implements ICommand<ChannelData> {

    Cache<String, Proxy<ChannelData>> cache;

    public Heartbeat() {
        cache = CacheBuilder.newBuilder()
                .maximumSize(2)
                .expireAfterWrite(30, TimeUnit.SECONDS)
                .removalListener(new RemovalListener<String, Proxy<ChannelData>>() {
                    @Override
                    public void onRemoval(RemovalNotification<String, Proxy<ChannelData>> notification) {
                        RemovalCause cause = notification.getCause();
                        if (cause == RemovalCause.EXPIRED) {
                            Proxy<ChannelData> proxy = notification.getValue();
                            proxy.setRunning(false);
                        }
                    }
                })
                .build();

    }

    @Override
    public void send(Proxy<ChannelData> proxy, ChannelData data) {
        cache.put(proxy.getConfig().getName(), proxy);
        proxy.send(data);
    }

    @Override
    public void execute(Proxy<ChannelData> proxy, ChannelData data) {
        cache.invalidate(proxy.getConfig().getName());
        proxy.setRunning(true);
    }

}
