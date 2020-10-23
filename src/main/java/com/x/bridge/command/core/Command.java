package com.x.bridge.command.core;

import com.x.bridge.command.impl.*;

/**
 * @Desc TODO
 * @Date 2020/10/22 21:00
 * @Author AD
 */
public enum Command {
    ConnectRequest(1, new ConnectRequestCommand()),
    ConnectSuccess(2, new ConnectSuccessCommand()),
    ConnectFailed(3, new ConnectFailedCommand()),
    Disconnect(4, new DisconnectCommand()),
    SendData(5, new SendDataCommand());
    
    private final int cmd;
    
    private final ICommand actor;
    
    private Command(int cmd, ICommand actor) {
        this.cmd = cmd;
        this.actor = actor;
    }
    
    public int getCmd() {
        return cmd;
    }
    
    public ICommand getActor() {
        return actor;
    }
}
