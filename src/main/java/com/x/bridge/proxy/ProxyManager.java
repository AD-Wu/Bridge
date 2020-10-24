package com.x.bridge.proxy;

import com.x.bridge.proxy.core.IBridge;
import com.x.bridge.proxy.core.Proxy;
import com.x.bridge.proxy.data.ProxyConfig;
import com.x.doraemon.util.Strings;

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
    
    public static void startProxy(ProxyConfig config) {
        String proxyAddress = config.getProxyAddress();
        IBridge bridge = BridgeManager.getBridge(config.getBridge());
        if (Strings.isNotNull(proxyAddress)) {
            new Proxy(proxyAddress, bridge, true);
        }else{
            new Proxy(proxyAddress, bridge, false);
        }
    }
    
    public static Proxy getProxy(String proxyName) {
        return proxies.get(proxyName);
    }
    
    public static void addProxy(String proxyName, Proxy proxy) {
        proxies.put(proxyName, proxy);
    }
    
}
