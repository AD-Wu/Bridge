package com.x.bridge.proxy.core;

/**
 * @Desc
 * @Date 2020/10/22 19:07
 * @Author AD
 */
public interface IBridge<ChannelData> {
    
    void send(ChannelData data) throws Exception;
    
}