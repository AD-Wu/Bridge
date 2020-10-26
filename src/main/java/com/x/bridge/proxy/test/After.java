package com.x.bridge.proxy.test;

import com.x.bridge.proxy.ProxyManager;
import com.x.bridge.proxy.data.ProxyConfig;
import com.x.bridge.proxy.data.ProxyConfigs;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Desc TODO
 * @Date 2020/10/26 21:04
 * @Author AD
 */
@Component
public class After implements InitializingBean {
    
    @Autowired
    private ProxyConfigs configs;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        for (ProxyConfig config : configs.getConfigs()) {
            ProxyManager.startProxy(config);
        }
    }
    
}
