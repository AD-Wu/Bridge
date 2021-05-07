package com.x.bridge.proxy.command.core;

import com.x.bridge.proxy.core.Proxy;

/**
 * @Desc
 * @Date 2021/4/29 22:30
 * @Author AD
 */
public interface ICommand<T> {
    
    void send(Proxy<T> proxy, T t);
    
    void execute(Proxy<T> proxy, T t);
    
}
