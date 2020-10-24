package com.x.bridge.proxy;

import com.x.bridge.proxy.core.ProxyClient;
import com.x.bridge.proxy.core.ProxyServer;
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
    
    private static Map<String, ProxyServer> servers = new ConcurrentHashMap<>();
    
    private static Map<String, ProxyClient> clients = new ConcurrentHashMap<>();
    
    private ProxyManager() {}
    
    public static void startProxy(ProxyConfig config) throws Exception {
        if(Strings.isNull(config.getProxyAddress())){
            ProxyClient client = new ProxyClient(config);
            client.start();
            clients.put(config.getName(), client);
        }else{
            ProxyServer server = new ProxyServer(config);
            server.start();
            servers.put(config.getName(), server);
        }
    }
    
    public static void stopProxyServer(String proxyName) throws Exception {
        ProxyServer server = servers.remove(proxyName);
        if (server != null) {
            server.stop();
        }
    }
    
    public static ProxyServer getProxyServer(String proxyName) {
        return servers.get(proxyName);
    }
    
    public static ProxyClient getProxyClient(String proxyName) {
        return clients.get(proxyName);
    }
    
    public static void addProxyClient(String proxyName, ProxyClient client) {
        clients.put(proxyName, client);
    }
    
}
