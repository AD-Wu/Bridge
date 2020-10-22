package com.x.bridge.data;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

/**
 * @Desc TODO
 * @Date 2020/10/22 01:43
 * @Author AD
 */
@Data
@ToString
@Configuration
@ConfigurationProperties(prefix = "bridge")
public class ProxyConfig {
    
    private int port;
    private String targetIP;
    private int targetPort;
    private Set<String> allowClients;
    
    private int connectTimeout;// 超时时间，单位:秒
    
    public boolean isAllowClient(String remoteIP){
        return allowClients.contains(remoteIP);
    }
    
}
