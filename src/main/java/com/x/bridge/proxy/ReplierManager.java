package com.x.bridge.proxy;

import com.x.bridge.proxy.server.Proxy;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Desc
 * @Date 2020/10/22 19:44
 * @Author AD
 */
@Data
public final class ReplierManager {
    
    private final Proxy proxy;
    private final Map<String, Replier> repliers;
    
    public ReplierManager(String proxyAddress){
        this.proxy = ProxyManager.getProxy(proxyAddress);
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
