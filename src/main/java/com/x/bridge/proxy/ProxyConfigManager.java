package com.x.bridge.proxy;

import com.x.bridge.proxy.data.ProxyConfig;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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
@Configuration
@ConfigurationProperties(prefix = "bridge")
public class ProxyConfigManager {
    
    private static Map<String, ProxyConfig> nameMap;
    
    private static Map<String, ProxyConfig> proxyAddressMap;
    
    @Autowired
    private List<ProxyConfig> configs;// configs这个名称要和yml文件bridge下的名称对应
    
    @PostConstruct
    private void convert() {
        nameMap = new ConcurrentHashMap<>();
        for (ProxyConfig config : configs) {
            nameMap.put(config.getName(), config);
            if(config.getProxyAddress()!=null){
                proxyAddressMap.put(config.getProxyAddress(), config);
            }
        }
    }
    
    public static ProxyConfig getProxyConfig(String proxyName) {
        return nameMap.get(proxyName);
    }
    
    public static ProxyConfig getProxyConfigByProxyAddress(String proxyAddress){
        return proxyAddressMap.get(proxyAddress);
    }
    
}
