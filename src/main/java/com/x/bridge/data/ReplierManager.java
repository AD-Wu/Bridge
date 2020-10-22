package com.x.bridge.data;

import com.x.bridge.proxy.Replier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Desc TODO
 * @Date 2020/10/22 19:44
 * @Author AD
 */
public class ReplierManager {
    
    private final int serverPort;
    private final Map<String, Replier> repliers;
    
    public ReplierManager(int serverPort){
        this.serverPort = serverPort;
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
