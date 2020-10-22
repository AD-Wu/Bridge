package com.x.bridge.data;

import com.x.bridge.command.Command;

/**
 * @Desc TODO
 * @Date 2020/10/22 19:07
 * @Author AD
 */
public interface IBridge<ChannelData> {
    
    void send(ChannelData data) throws Exception;
    
}
