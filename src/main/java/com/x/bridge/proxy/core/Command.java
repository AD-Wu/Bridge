package com.x.bridge.proxy.core;

import com.x.bridge.data.ChannelData;
import com.x.bridge.proxy.command.*;
import com.x.bridge.proxy.command.core.ICommand;
import lombok.extern.log4j.Log4j2;

/**
 * @Desc
 * @Date 2020/10/22 21:00
 * @Author AD
 */
@Log4j2
public enum Command {
    Disconnect(new Disconnect()),
    Heartbeat(new Heartbeat()),
    SendData(new SendData()),
    SyncClientConnect(new SyncConnectResponse()),
    SyncServerConnect(new SyncConnectRequest());
    private final ICommand<ChannelData> actor;
    
    private Command(ICommand<ChannelData> actor) {
        this.actor = actor;
    }
    
    public ICommand<ChannelData> getActor() {
        return this.actor;
    }
}
