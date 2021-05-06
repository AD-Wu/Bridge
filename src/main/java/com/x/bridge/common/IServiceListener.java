package com.x.bridge.common;

/**
 * @Desc
 * @Date 2021/4/29 20:19
 * @Author AD
 */
public interface IServiceListener {
    
    void onServiceStart(IService service);
    
    void onServiceStop(IService service);
    
    void onServiceError(IService service, Throwable t);
    
}
