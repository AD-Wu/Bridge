package com.x.bridge.proxy.util;

import java.util.concurrent.ThreadFactory;

/**
 * @Desc
 * @Date 2021/4/25 17:57
 * @Author AD
 */
public class ProxyThreadFactory implements ThreadFactory {

    private final String threadPrefixName;

    public ProxyThreadFactory(String threadPrefixName) {
        this.threadPrefixName = threadPrefixName;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, threadPrefixName);
    }

}
