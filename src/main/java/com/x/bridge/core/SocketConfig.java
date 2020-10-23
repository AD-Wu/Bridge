package com.x.bridge.core;

import lombok.Data;

import java.net.InetAddress;
import java.net.UnknownHostException;

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
        try {
            return new SocketConfig(InetAddress.getByName(ip).getHostAddress(), port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;

    }
    
    public static SocketConfig serverConfig(int port) {

        try {
            return new SocketConfig(InetAddress.getLocalHost().getHostAddress(), port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private SocketConfig(String ip, int port) {
        this.ip = ip;
        this.port = port<0?0:port;
    }
    
}
