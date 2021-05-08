package com.x.bridge.proxy.command;

import com.google.common.cache.*;
import com.x.bridge.common.IFactory;
import com.x.bridge.data.ChannelData;
import com.x.bridge.data.ProxyConfig;
import com.x.bridge.data.ProxyThreadFactory;
import com.x.bridge.proxy.command.core.ICommand;
import com.x.bridge.proxy.core.Command;
import com.x.bridge.proxy.core.Proxy;
import lombok.extern.log4j.Log4j2;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Desc
 * @Date 2021/5/6 19:52
 * @Author AD
 */
@Log4j2
public class Heartbeat implements ICommand<ChannelData>, IFactory<Proxy<ChannelData>, ChannelData> {

    private Cache<String, Proxy<ChannelData>> cache;
    private ScheduledExecutorService timer;
    private AtomicLong cursor;

    public Heartbeat() {
        cursor = new AtomicLong(1);
        timer = Executors.newScheduledThreadPool(1, new ProxyThreadFactory("Heartbeat-"));
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


    public void start(Proxy<ChannelData> proxy) throws Exception {

    }


    public void stop() throws Exception {

    }

    @Override
    public ChannelData get(Proxy<ChannelData> proxy) {
        ProxyConfig config = proxy.getConfig();
        ChannelData cd = new ChannelData();
        cd.setAppClient(config.getName());
        cd.setRecvSeq(getSeq());
        cd.setCommand(Command.Heartbeat);
        String timezone = TimeZone.getDefault().getID();
        String content = timezone+" "+ LocalDateTime.now();
        byte[] data = content.getBytes(StandardCharsets.UTF_8);
        cd.setData(data);
        return cd;
    }

    private long getSeq() {
        if (cursor.get() >= Long.MAX_VALUE) {
            cursor.set(1);
        }
        return cursor.getAndIncrement();
    }

}
