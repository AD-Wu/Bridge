package com.x.bridge.common;

/**
 * @Desc
 * @Date 2021/4/29 20:22
 * @Author AD
 */
public interface IReceiver<T> {
    
    void onReceive(T... ts);
    
}
