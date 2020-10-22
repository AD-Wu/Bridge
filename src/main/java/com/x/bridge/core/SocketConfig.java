package com.x.bridge.core;

import lombok.Data;

/**
 * @Desc TODO
 * @Date 2020/10/21 19:49
 * @Author AD
 */
@Data
public class SocketConfig {
    
    private final String ip;
    
    private final int port;
    
    private int readTimeout = 0;
    
    private int writeTimeout = 0;
    
    private int idleTimeout = 0;
    
    private int backlog = 2048;
    
    private int recvBuf = 65536;
    
    public static SocketConfig clientConfig(String ip, int port) {
        return new SocketConfig(ip, port);
    }
    
    public static SocketConfig serverConfig(int port) {
        return new SocketConfig("localhost", port);
    }
    
    private SocketConfig(String ip, int port) {
        this.ip = ip;
        this.port = port<0?0:port;
    }
    
}
