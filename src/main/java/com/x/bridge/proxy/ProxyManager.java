package com.x.bridge.proxy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Desc TODO
 * @Date 2020/10/22 01:26
 * @Author AD
 */
public final class ProxyManager {
    
    private Map<Integer,Proxy> proxies;
    
   
    
    public ProxyManager(){
        this.proxies = new ConcurrentHashMap<>();
    }
    
    
}
