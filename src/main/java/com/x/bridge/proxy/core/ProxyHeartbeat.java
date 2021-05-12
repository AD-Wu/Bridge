package com.x.bridge.proxy.core;

import com.google.common.cache.*;
import com.x.bridge.data.ChannelData;
import com.x.bridge.data.ProxyConfig;
import com.x.bridge.data.ProxyThreadFactory;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Desc
 * @Date 2021/5/11 14:55
 * @Author AD
 */
public class ProxyHeartbeat {
    
    private volatile boolean started;
    private final Proxy<ChannelData> proxy;
    private final AtomicLong cursor;
    private final ScheduledExecutorService timer;
    private final Cache<String, Proxy<ChannelData>> cache;
    
    public ProxyHeartbeat(Proxy<ChannelData> proxy) {
        this.started = false;
        this.proxy = proxy;
        this.cursor = new AtomicLong(1);
        this.timer = Executors.newScheduledThreadPool(1, new ProxyThreadFactory("ProxyHeartbeat-"));
        this.cache = CacheBuilder.newBuilder()
                .maximumSize(2)
                .expireAfterWrite(Math.max(30, proxy.getConfig().getHeartbeat()), TimeUnit.SECONDS)
                .removalListener(new RemovalListener<String, Proxy<ChannelData>>() {
                    @Override
                    public void onRemoval(RemovalNotification<String, Proxy<ChannelData>> notification) {
                        if (notification.getCause() == RemovalCause.EXPIRED) {
                            proxy.setRunning(false);
                        }
                    }
                })
                .build();
    }
    
    public synchronized void start() {
        if (started) {
            return;
        }
        timer.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                cache.put(proxy.getConfig().getName(), proxy);
                proxy.send(getData());
            }
        }, 30, Math.max(30, proxy.getConfig().getHeartbeat()), TimeUnit.SECONDS);
        started = true;
    }
    
    public synchronized void stop() {
        if (started) {
            timer.shutdown();
            started = false;
        }
    }
    
    private ChannelData getData() {
        ProxyConfig config = proxy.getConfig();
        ChannelData cd = new ChannelData();
        cd.setAppClient(config.getName());
        cd.setRecvSeq(getSeq());
        cd.setCommand(Command.Heartbeat);
        String timezone = TimeZone.getDefault().getID();
        String content = timezone + " " + LocalDateTime.now();
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
