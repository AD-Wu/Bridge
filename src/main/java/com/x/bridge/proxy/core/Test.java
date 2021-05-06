package com.x.bridge.proxy.core;

/**
 * @Desc TODO
 * @Date 2021/5/3 19:42
 * @Author AD
 */
public enum Test {
    
    A<ChannelData>{
    
    };
    
    public abstract <T> void execute1(Proxy proxy, T t) throws Exception;
}
