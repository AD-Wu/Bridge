package com.x.bridge.proxy;

import com.x.bridge.data.ProxyConfig;

import java.util.Map;

/**
 * @Desc TODO
 * @Date 2020/10/22 01:42
 * @Author AD
 */
public class ProxyConfigManager {
    
    private static Map<String, ProxyConfig> configs;
    
    public static ProxyConfig getProxyConfig(String proxyAddress){
        return configs.get(proxyAddress);
    }
    
}
