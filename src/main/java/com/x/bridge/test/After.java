package com.x.bridge.test;

import com.x.bridge.proxy.ProxyConfigs;
import com.x.bridge.proxy.ProxyManager;
import com.x.bridge.proxy.data.ProxyConfig;
import com.x.doraemon.util.ArrayHelper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Desc TODO
 * @Date 2020/10/22 01:57
 * @Author AD
 */
@Component
@Log4j2
public class After implements InitializingBean {
    
    @Autowired
    private ProxyConfigs configs;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        if (configs != null) {
            List<ProxyConfig> configs = this.configs.getConfigs();
            if (ArrayHelper.isEmpty(configs)) {
                log.info("无配置，不启动代理服务器");
            } else {
                for (ProxyConfig config : configs) {
                    ProxyManager.startProxy(config);
                }
            }
            
        }
    }
    
}
