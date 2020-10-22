package com.x.bridge.proxy;

import com.x.bridge.core.SocketConfig;
import com.x.bridge.core.SocketServer;

/**
 * @Desc TODO
 * @Date 2020/10/22 01:07
 * @Author AD
 */
public class Proxy {
    
    private SocketServer server;
    public Proxy(int port){
        this.server = new SocketServer(SocketConfig.serverConfig(port),new ProxyServerListener());
    }
}
