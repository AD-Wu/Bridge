package com.x.bridge.proxy.bridge.core;

/**
 * @Desc
 * @Date 2020/10/22 19:07 * @Author AD
 */
public interface IBridge<T> {

    String name();

    void start() throws Exception;

    void stop() throws Exception;

    void send(T data) throws Exception;

}
