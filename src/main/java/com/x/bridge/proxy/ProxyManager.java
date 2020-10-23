package com.x.bridge.proxy;

import com.x.bridge.proxy.core.Proxy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Desc
 * @Date 2020/10/22 01:26
 * @Author AD
 */
public final class ProxyManager {
    
    // key=代理地址，value=代理对象
    private static Map<String, Proxy> proxies = new ConcurrentHashMap<>();
    
    private ProxyManager() {}
    
    public static Proxy getProxy(String proxyAddress) {
        return proxies.get(proxyAddress);
    }
    
}
