package com.x.bridge.data;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Desc TODO
 * @Date 2020/10/22 01:42
 * @Author AD
 */
@Data
// @Configuration
// @ConfigurationProperties(prefix = "bridge")
public class ProxyConfigManager {
    
    private static Map<String, ProxyConfig> configMap;
    
    @Autowired
    private List<ProxyConfig> configs;// configs这个名称要和yml文件bridge下的名称对应

    @PostConstruct
    private void convert() {
        configMap = new ConcurrentHashMap<>();
        for (ProxyConfig config : configs) {
            configMap.put(config.getName(), config);
        }
    }
    
    public static ProxyConfig get(String proxyName) {
        return configMap.get(proxyName);
    }
    
}
