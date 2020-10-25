package com.x.bridge.proxy.data;

/**
 * @Desc TODO
 * @Date 2020/10/24 18:56
 * @Author AD
 */
public enum MessageType {
    ClientToServer(1),
    ServerToClient(2);
    
    
    public static MessageType getMessageType(int code){
        if(code==1){
            return ClientToServer;
        }
        return ServerToClient;
    }
    
    private final int code;
    
    private MessageType(int code) {
        this.code = code;
    }
    
    public int getCode() {
        return code;
    }
}
