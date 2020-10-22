package com.x.bridge.data;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.util.StringJoiner;

/**
 * @Desc TODO
 * @Date 2020/10/21 23:28
 * @Author AD
 */
@Getter
@ToString
public final class ChannelInfo {
    
    private final String remoteAddress;
    
    private final String localAddress;
    
    private final String remoteIP;
    
    private final int remotePort;
    
    private final String localIP;
    
    private final int localPort;
    
    public ChannelInfo(String remoteAddress, String localAddress) {
        this.remoteAddress = remoteAddress;
        this.localAddress = localAddress;
        String[] remotes = remoteAddress.split(":");
        this.remoteIP = remotes[0];
        this.remotePort = Integer.parseInt(remotes[1]);
        String[] locals = localAddress.split(":");
        this.localIP = locals[0];
        this.localPort = Integer.parseInt(locals[1]);
    }
    
}
