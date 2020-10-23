package com.x.bridge.data;

import com.x.bridge.proxy.ProxyManager;
import com.x.bridge.proxy.Replier;
import com.x.bridge.proxy.server.Proxy;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Desc TODO
 * @Date 2020/10/22 19:44
 * @Author AD
 */
@Data
public class ReplierManager {
    
    private final int serverPort;
    private final Proxy proxy;
    private final Map<String, Replier> repliers;
    
    public ReplierManager(int serverPort){
        this.serverPort = serverPort;
        this.proxy = ProxyManager.getProxyServer(serverPort);
        this.repliers = new ConcurrentHashMap<>();
    }
    
    public void addReplier(String remoteAddress,Replier replier){
        repliers.put(remoteAddress, replier);
    }
    
    public Replier getReplier(String remoteAddress){
        return repliers.get(remoteAddress);
    }
    
    public Replier removeReplier(String remoteAddress){
        return repliers.remove(remoteAddress);
    }
    
}
