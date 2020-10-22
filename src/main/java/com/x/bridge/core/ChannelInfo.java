package com.x.bridge.core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;

import java.util.StringJoiner;

/**
 * @Desc TODO
 * @Date 2020/10/21 23:28
 * @Author AD
 */
@Getter
public final class ChannelInfo {
    
    private final String remoteAddress;
    
    private final String localAddress;
    
    private final ChannelHandlerContext ctx;
    
    private final Channel channel;
    
    public ChannelInfo(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        this.channel = ctx.channel();
        this.remoteAddress = channel.remoteAddress().toString().substring(1);
        this.localAddress = channel.localAddress().toString().substring(1);
    }
    
    @Override
    public String toString() {
        return new StringJoiner(", ", ChannelInfo.class.getSimpleName() + "[", "]")
                .add("remoteAddress='" + remoteAddress + "'")
                .add("localAddress='" + localAddress + "'")
                .toString();
    }
    
}
