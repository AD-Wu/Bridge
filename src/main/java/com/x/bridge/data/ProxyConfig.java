package com.x.bridge.data;

import com.x.doraemon.util.StringHelper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

/**
 * @Desc
 * @Date 2020/10/22 01:43
 * @Author AD
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "proxy")
public class ProxyConfig {
    
    private String name;
    private boolean out;
    private String proxyServer;
    private String appServer;
    private int connectTimeout;// 超时时间，单位:秒
    private int heartbeat = 30;// 心跳周期，单位:秒
    private boolean keepAlive = true;
    private Set<String> allowClients;
    
    private boolean open=false;
    private boolean needClientAuth = false;
    private String keyStoreType;
    private String keyStore;
    private String keyStorePassword;
    private String trustKeyStore;
    private String trustKeyStorePassword;
    
    private String sendType;
    
    @Override
    public String toString() {
        return StringHelper.defaultToString(this);
    }
    
}
