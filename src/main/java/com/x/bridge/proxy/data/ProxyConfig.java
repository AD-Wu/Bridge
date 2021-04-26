package com.x.bridge.proxy.data;

import com.x.doraemon.util.ArrayHelper;
import lombok.Data;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

/**
 * @Desc
 * @Date 2020/10/22 01:43
 * @Author AD
 */
@Data
@Configuration
public class ProxyConfig {
    
    protected String name;
    protected String bridge;
    protected String proxyServer;
    protected String appSocketServer;
    protected Set<String> allowClients;
    protected int connectTimeout;// 超时时间，单位:秒
    
    public boolean isAllowClient(String remoteIP){
        if(ArrayHelper.isEmpty(allowClients)){
            return true;
        }
        return allowClients.contains(remoteIP);
    }
    
}
