package com.x.bridge.command;

/**
 * @Desc TODO
 * @Date 2020/10/22 21:00
 * @Author AD
 */
public enum Command {
    ConnectRequest(1),
    ConnectSuccess(2),
    ConnectFailed(3),
    Disconnect(4),
    SendData(5);
    
    private final int cmd;
    
    private Command(int cmd) {
        this.cmd = cmd;
    }
    
    public int getCmd() {
        return cmd;
    }
    
}
