package com.x.bridge.common;

/**
 * @Desc
 * @Date 2021/4/29 20:18
 * @Author AD
 */
public interface IService {
    
    String name();
    
    void start() throws Exception;
    
    void stop() throws Exception;
    
}
