package com.x.bridge.proxy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Desc TODO
 * @Date 2020/10/22 01:26
 * @Author AD
 */
public final class ProxyManager {

    // key=端口，value=代理对象
    private static Map<Integer,Proxy> proxies;
    
    public ProxyManager(){
        this.proxies = new ConcurrentHashMap<>();
    }

    public static Proxy getProxy(int serverPort){
        return proxies.get(serverPort);
    }
    
    
}
