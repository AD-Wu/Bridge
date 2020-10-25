package com.x.bridge.proxy.bridge;

import com.x.bridge.proxy.ProxyConfigs;
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
public final class BridgeManager {
    
    @Autowired
    private ProxyConfigs roxyConfigs;
    
    @PostConstruct
    private void init() {
        ServiceLoader<IBridge> load = ServiceLoader.load(IBridge.class);
        Iterator<IBridge> it = load.iterator();
        while (it.hasNext()) {
            IBridge b = it.next();
            bridges.put(b.name(), b);
        }
        for (ProxyConfig config : roxyConfigs.getConfigs()) {
            configs.put(config.getName(), config);
        }
    }
    
    public static Map<String, ProxyConfig> configs = new ConcurrentHashMap<>();
    
    public static Map<String, IBridge> bridges = new ConcurrentHashMap<>();
    
    public static Map<String, IBridge> proxyBridges = new ConcurrentHashMap<>();
    
    public static IBridge getBridge(String proxyName) {
        if (!proxyBridges.containsKey(proxyName)) {
            synchronized (BridgeManager.class) {
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
