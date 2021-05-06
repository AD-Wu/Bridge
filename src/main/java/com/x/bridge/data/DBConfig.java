package com.x.bridge.data;

import com.x.doraemon.util.StringHelper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Desc
 * @Date 2021/4/13 12:49
 * @Author AD
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "database.config")
public class DBConfig extends ProxyConfig {
    
    private String poolType;
    private String databaseType;
    private String url;
    private String user;
    private String password;
    private String driverClass;
    private boolean out;
    private String outWriteInTable;
    private String inWriteOutTable;
    
    
    @Override
    public String toString() {
        return StringHelper.defaultToString(this);
    }
    
}
