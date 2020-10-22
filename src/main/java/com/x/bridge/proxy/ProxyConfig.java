package com.x.bridge.proxy;

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
    
}
