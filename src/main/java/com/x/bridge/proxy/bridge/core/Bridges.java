package com.x.bridge.proxy.bridge.core;

import com.x.bridge.proxy.data.ProxyConfigs;
import com.x.bridge.proxy.data.ProxyConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Desc
 * @Date 2020/10/24 15:49
 * @Author AD
 */
@Component
public final class Bridges {
    
    @Autowired
    private ProxyConfigs proxyConfigs;
    
    @PostConstruct
    private void init() {
        ServiceLoader<IBridge> load = ServiceLoader.load(IBridge.class);
        Iterator<IBridge> it = load.iterator();
        while (it.hasNext()) {
            IBridge b = it.next();
            bridges.put(b.name(), b);
        }
        for (ProxyConfig config : proxyConfigs.getConfigs()) {
            configs.put(config.getName(), config);
        }
    }
    
    public static Map<String, ProxyConfig> configs = new ConcurrentHashMap<>();
    
    public static Map<String, IBridge> bridges = new ConcurrentHashMap<>();
    
    public static Map<String, IBridge> proxyBridges = new ConcurrentHashMap<>();
    
    public static IBridge getBridge(String proxyName) {
        if (!proxyBridges.containsKey(proxyName)) {
            synchronized (Bridges.class) {
                if (!proxyBridges.containsKey(proxyName)) {
                    ProxyConfig config = configs.get(proxyName);
                    IBridge bridge = bridges.get(config.getBridge());
                    IBridge newBridge = bridge.newInstance();
                    proxyBridges.put(proxyName, newBridge);
                }
            }
        }
        return proxyBridges.get(proxyName);
    }
    
}
