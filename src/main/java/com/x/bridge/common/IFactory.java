package com.x.bridge.common;

/**
 * @Desc
 * @Date 2021/5/8 00:40
 * @Author AD
 */
public interface IFactory<T,R> {
    
    R get(T t);
}
