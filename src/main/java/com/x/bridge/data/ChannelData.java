package com.x.bridge.data;

import com.x.bridge.command.core.Command;
import lombok.Data;

/**
 * @Desc TODO
 * @Date 2020/10/22 22:02
 * @Author AD
 */
@Data
public class ChannelData {
    
    private String appSocketClient;
    
    private String proxyAddress;
    
    private String targetAddress;
    
    private long recvSeq;
    
    private Command cmd;
    
    private byte[] data;
    
    private String targetIP;
    
    private int targetPort;
    
    public void setTargetAddress(String targetAddress) {
        this.targetAddress = targetAddress;
        String[] targets = targetAddress.split(":");
        this.targetIP = targets[0];
        this.targetPort = Integer.parseInt(targets[1]);
    }
    
}
