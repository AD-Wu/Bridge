package com.x.bridge.proxy;

import com.x.bridge.data.ProxyConfig;

import java.util.Map;
import java.util.Set;

/**
 * @Desc TODO
 * @Date 2020/10/22 01:42
 * @Author AD
 */
public class ProxyConfigManager {
    
    private static Map<Integer, ProxyConfig> configs;
    
    public static ProxyConfig getProxyConfig(int serverPort){
        return configs.get(serverPort);
    }
    
}
