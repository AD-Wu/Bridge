package com.x.bridge.data;

import com.x.doraemon.util.ArrayHelper;
import lombok.Data;

import java.util.Set;

/**
 * @Desc
 * @Date 2020/10/22 01:43
 * @Author AD
 */
@Data
// @Configuration
public class ProxyConfig {
    
    public String name;
    public String bridge;
    public String proxyServer;
    public String appSocketServer;
    public Set<String> allowClients;
    public int connectTimeout;// 超时时间，单位:秒
    
    public boolean isAllowClient(String remoteIP){
        if(ArrayHelper.isEmpty(allowClients)){
            return true;
        }
        return allowClients.contains(remoteIP);
    }
    
}
