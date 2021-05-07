package com.x.bridge.proxy.command;

import com.x.bridge.data.ChannelData;
import com.x.bridge.proxy.command.core.ICommand;
import com.x.bridge.proxy.core.Proxy;
import com.x.doraemon.util.StringHelper;
import lombok.extern.log4j.Log4j2;

/**
 * @Desc TODO
 * @Date 2021/5/6 19:52
 * @Author AD
 */
@Log4j2
public class Heartbeat implements ICommand<ChannelData> {
    
    @Override
    public void request(Proxy<ChannelData> proxy, ChannelData data) {
        try {
            proxy.send(data);
        } catch (Exception e) {
            log.error(StringHelper.getExceptionTrace(e));
            proxy.setRunning(false);
        }
    }
    
    @Override
    public void response(Proxy<ChannelData> proxy, ChannelData data) {
        proxy.setRunning(true);
    }
    
}
