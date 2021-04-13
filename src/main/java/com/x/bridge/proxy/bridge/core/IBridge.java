package com.x.bridge.proxy.bridge.core;

import com.x.bridge.common.IService;

/**
 * @Desc
 * @Date 2020/10/22 19:07 * @Author AD
 */
public interface IBridge<T> extends IService {
    
    String name();
    
    void send(T data) throws Exception;
    
    IBridge<T> newInstance();
    
}
