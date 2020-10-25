package com.x.bridge.proxy.bridge;

import com.x.bridge.core.IService;

/**
 * @Desc
 * @Date 2020/10/22 19:07
 * @Author AD
 */
public interface IBridge<ChannelData> extends IService {
    
    void send(ChannelData data) throws Exception;
    
    boolean isStart();
    
    String bridgeName();
    
    IBridge newInstance();
    
}
