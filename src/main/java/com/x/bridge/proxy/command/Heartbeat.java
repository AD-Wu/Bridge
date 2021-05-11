package com.x.bridge.proxy.command;

import com.x.bridge.data.ChannelData;
import com.x.bridge.proxy.command.core.ICommand;
import com.x.bridge.proxy.core.Proxy;
import lombok.extern.log4j.Log4j2;

/**
 * @Desc
 * @Date 2021/5/6 19:52
 * @Author AD
 */
@Log4j2
public class Heartbeat implements ICommand<ChannelData> {


    @Override
    public void send(Proxy<ChannelData> proxy, ChannelData data) {
        proxy.send(data);
    }

    @Override
    public void execute(Proxy<ChannelData> proxy, ChannelData data) {
        proxy.setRunning(true);
    }

}
