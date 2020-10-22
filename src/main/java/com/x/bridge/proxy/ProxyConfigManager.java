package com.x.bridge.proxy;

import java.util.Map;
import java.util.Set;

/**
 * @Desc TODO
 * @Date 2020/10/22 01:42
 * @Author AD
 */
public class ProxyConfigManager {
    
    private static Map<Integer, ProxyConfig> configs;
    
    public static boolean isAllowClient(int localPort, String remoteIP) {
        ProxyConfig proxyConfig = configs.get(localPort);
        Set<String> allowClients = proxyConfig.getAllowClients();
        if (allowClients == null || allowClients.size() == 0) {
            return true;
        }
        return proxyConfig.getAllowClients().contains(remoteIP);
    }
    
}
