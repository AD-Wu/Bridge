package com.x.bridge.proxy.bridge.core;

import com.x.bridge.data.ChannelData;

/**
 * @Desc TODO
 * @Date 2021/4/25 20:52
 * @Author AD
 */
public interface IReceiver {
    
    void receive(ChannelData... datas)throws Exception;
}
