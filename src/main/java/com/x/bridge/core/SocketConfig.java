package com.x.bridge.core;

import com.x.doraemon.util.Strings;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Desc TODO
 * @Date 2020/10/21 19:49
 * @Author AD
 */
@Data
@Log4j2
public class SocketConfig {
    
    private String ip;
    
    private int port;
    
    private int readTimeout = 0;
    
    private int writeTimeout = 0;
    
    private int idleTimeout = 0;
    
    private int backlog = 2048;
    
    private int recvBuf = 65536;
    
    public SocketConfig(int port) {
        try {
            this.ip = InetAddress.getLocalHost().getHostAddress();
            this.port = port < 0 ? 0 : port;
        } catch (UnknownHostException e) {
            log.error(Strings.getExceptionTrace(e));
        }
    }
    
    public SocketConfig(String ip, int port) {
        try {
            this.ip = InetAddress.getByName(ip).getHostAddress();
            this.port = port < 0 ? 0 : port;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
    
}
