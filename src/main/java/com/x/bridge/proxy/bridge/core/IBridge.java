package com.x.bridge.proxy.bridge.core;

import com.x.bridge.proxy.data.ChannelData;

/**
 * @Desc
 * @Date 2020/10/22 19:07 * @Author AD
 */
public interface IBridge{
    
    String name();
    
    void start() throws Exception;
    
    void stop() throws Exception;

    void send(ChannelData data) throws Exception;
    
    void addReceiver(IReceiver receiver);
    
}
