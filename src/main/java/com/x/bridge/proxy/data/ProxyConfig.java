package com.x.bridge.proxy.data;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

/**
 * @Desc TODO
 * @Date 2020/10/22 01:43
 * @Author AD
 */
@Data
@Configuration
public class ProxyConfig {
    
    private String name;
    private String bridge;
    private String proxyAddress;
    private String targetAddress;
    private Set<String> allowClients;
    
    private int connectTimeout;// 超时时间，单位:秒
    
    public boolean isAllowClient(String remoteIP){
        return allowClients.contains(remoteIP);
    }
    
}
