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
    SyncConnectRequest(new SyncConnectRequest()),
    SyncConnectSuccess(new SyncConnectSuccess()),
    SyncConnectError(new SyncConnectError()),
    SyncConnectTimeout(new SyncConnectTimeout());

    private final ICommand<ChannelData> cmd;

    private Command(ICommand<ChannelData> cmd) {
        this.cmd = cmd;
    }

    public ICommand<ChannelData> get() {
        return this.cmd;
    }

}
