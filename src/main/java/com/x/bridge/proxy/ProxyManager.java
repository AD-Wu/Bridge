package com.x.bridge.proxy;

import com.x.bridge.proxy.core.ProxyClient;
import com.x.bridge.proxy.core.ProxyServer;
import com.x.bridge.proxy.data.ProxyConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Desc
 * @Date 2020/10/22 01:26
 * @Author AD
 */
public final class ProxyManager {
    
    private static Map<String, ProxyServer> servers = new ConcurrentHashMap<>();
    
    private static Map<String, ProxyClient> clients = new ConcurrentHashMap<>();
    
    private ProxyManager() {}
    
    public static void startProxyServer(ProxyConfig config) throws Exception {
        // ProxyServer server = new ProxyServer(config);
        // server.start();
    }
    
    public static ProxyServer getProxyServer(String proxyName) {
        return servers.get(proxyName);
    }
    
    public static void addProxyServer(String proxyName, ProxyServer server) {
        servers.put(proxyName, server);
    }
    
    public static ProxyClient getProxyClient(String proxyAddress) {
        return clients.get(proxyAddress);
    }
    
    public static void addProxyClient(String proxyAddress, ProxyClient client) {
        clients.put(proxyAddress, client);
    }
    
}
