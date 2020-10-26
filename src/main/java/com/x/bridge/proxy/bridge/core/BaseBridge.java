package com.x.bridge.proxy.bridge.core;

import com.x.bridge.proxy.data.ChannelData;
import lombok.extern.log4j.Log4j2;

/**
 * @Desc TODO
 * @Date 2020/10/26 23:07
 * @Author AD
 */
@Log4j2
public abstract class BaseBridge implements IBridge<ChannelData>{
    
    
    protected volatile boolean started = false;
    
    @Override
    public void start() throws Exception {
        if(!started){
            synchronized (this){
                if(!started){
                    onStart();
                    started = true;
                }
            }
        }
    }
    
    @Override
    public void stop() throws Exception {
        if(started){
            synchronized (this){
                if(started){
                    onStop();
                    started = false;
                }
            }
        }
    }
    
    protected abstract void onStart() throws Exception;
    
    protected abstract void onStop() throws Exception;
    
}
