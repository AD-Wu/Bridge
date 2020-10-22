package com.x.bridge.test;

import com.x.bridge.data.ProxyConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Desc TODO
 * @Date 2020/10/22 01:57
 * @Author AD
 */
@Component
public class After implements InitializingBean {
    
    @Autowired
    private ProxyConfig config;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println(config);
    }
    
}
