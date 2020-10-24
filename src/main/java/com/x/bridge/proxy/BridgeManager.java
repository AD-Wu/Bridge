package com.x.bridge.proxy;

import com.x.bridge.proxy.core.IBridge;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Desc TODO
 * @Date 2020/10/24 15:49
 * @Author AD
 */
public final class BridgeManager {
    
    public static Map<String, IBridge> bridges = new ConcurrentHashMap<>();
    
    public static IBridge getBridge(String bridge) {
        return bridges.get(bridge);
    }
    
}
