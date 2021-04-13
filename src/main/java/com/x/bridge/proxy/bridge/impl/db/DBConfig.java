package com.x.bridge.proxy.bridge.impl.db;

import com.x.bridge.proxy.data.ProxyConfig;
import lombok.Data;

/**
 * @Desc
 * @Date 2021/4/13 12:49
 * @Author AD
 */
@Data
public class DBConfig extends ProxyConfig {


    private String url;

    private String driver;

    private String user;

    private String password;

    private boolean out;

    private String outWriteInTable;

    private String inWriteOutTable;

}
