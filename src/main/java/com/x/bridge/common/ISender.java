package com.x.bridge.common;

/**
 * @Desc
 * @Date 2021/4/29 20:20
 * @Author AD
 */
public interface ISender<T> extends IService {
    
    void send(T t) throws Exception;
    
    void setReceiver(IReceiver<T> receiver);
    
}
