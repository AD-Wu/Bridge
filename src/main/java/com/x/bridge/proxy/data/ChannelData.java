package com.x.bridge.proxy.data;

import com.x.bridge.command.core.Command;
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
    
    private String proxyName;
    
    private String appSocketClient;
    
    private String proxyAddress;
    
    private String targetAddress;
    
    private long recvSeq;
    
    private Command command;
    
    private MessageType messageType;
    
    private byte[] data;
    
}
