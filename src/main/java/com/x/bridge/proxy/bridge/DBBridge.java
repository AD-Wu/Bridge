package com.x.bridge.proxy.bridge;

import com.google.auto.service.AutoService;
import com.x.bridge.proxy.data.ChannelData;
import lombok.extern.log4j.Log4j2;

/**
 * @Desc
 * @Date 2020/10/25 00:08
 * @Author AD
 */
@Log4j2
@AutoService(IBridge.class)
public class DBBridge implements IBridge<ChannelData> {
    
    private volatile boolean started = false;
    
    @Override
    public void send(ChannelData cd) throws Exception {
        log.info("桥发送数据:{}" ,cd);
    }
    
    @Override
    public boolean isStart() {
        return started;
    }
    
    @Override
    public String bridgeName() {
        return "DB";
    }
    
    @Override
    public IBridge newInstance() {
        return new DBBridge();
    }
    
    @Override
    public void start() throws Exception {
        log.info("桥启动");
    }
    
    @Override
    public void stop() throws Exception {
        log.info("桥停止");
    }
    
}
