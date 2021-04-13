package com.x.bridge.proxy.test;

import com.x.bridge.proxy.ProxyManager;
import com.x.bridge.proxy.data.ProxyConfig;
import com.x.bridge.proxy.data.ProxyConfigManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Desc
 * @Date 2020/10/26 21:04
 * @Author AD
 */
@Component
public class After implements InitializingBean {
    
    @Autowired
    private ProxyConfigManager configs;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        for (ProxyConfig config : configs.getConfigs()) {
            ProxyManager.startProxyServer(config);
        }
    }
    
}
