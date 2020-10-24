package com.x.bridge.proxy.data;

import lombok.Builder;
import lombok.Data;

/**
 * @Desc TODO
 * @Date 2020/10/22 22:02
 * @Author AD
 */
@Data
@Builder
public class ChannelData {
    
    private String appSocketClient;
    
    private String proxyAddress;
    
    private String targetAddress;
    
    private long recvSeq;
    
    private int command;
    
    private byte[] data;
    
}
