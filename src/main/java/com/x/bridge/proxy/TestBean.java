package com.x.bridge.proxy;

import com.x.bridge.data.DBConfig;
import com.x.bridge.data.ProxyConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Desc
 * @Date 2021/5/2 13:51
 * @Author AD
 */
@Component
public class TestBean implements InitializingBean {
    
    @Autowired
    private ProxyConfig proxyConfig;
    @Autowired
    private DBConfig dbConfig;
    
    
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("代理配置：" + proxyConfig);
        System.out.println("数据库配置：" + dbConfig);
    
    }
    
}
