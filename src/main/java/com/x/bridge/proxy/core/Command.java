package com.x.bridge.proxy.core;

import com.x.bridge.data.ChannelData;
import lombok.extern.log4j.Log4j2;

/**
 * @Desc
 * @Date 2020/10/22 21:00
 * @Author AD
 */
@Log4j2
public enum Command {
    ConnectRequest {
        @Override
        public void execute(Proxy<ChannelData> proxy, ChannelData channelData) {
        
        }
    },
    ConnectSuccess {
        @Override
        public void execute(Proxy<ChannelData> proxy, ChannelData channelData) {
        
        }
    },
    ConnectFailed {
        @Override
        public void execute(Proxy<ChannelData> proxy, ChannelData channelData) {
        
        }
    },
    Disconnect {
        @Override
        public void execute(Proxy<ChannelData> proxy, ChannelData channelData) {
        
        }
    },
    SendData {
        @Override
        public void execute(Proxy<ChannelData> proxy, ChannelData channelData) {
        
        }
    },
    HeartbeatRequest {
        @Override
        public void execute(Proxy<ChannelData> proxy, ChannelData channelData) {
        
        }
    };
    
    public abstract void execute(Proxy<ChannelData> proxy, ChannelData channelData);
}
