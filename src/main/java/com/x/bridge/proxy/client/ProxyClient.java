package com.x.bridge.proxy.client;

import com.x.bridge.proxy.core.Command;
import com.x.bridge.proxy.core.Proxy;
import com.x.bridge.proxy.core.Replier;
import com.x.bridge.data.ChannelData;
import com.x.bridge.proxy.core.MessageType;
import com.x.bridge.data.ProxyConfig;
import com.x.doraemon.util.ArrayHelper;
import com.x.doraemon.util.StringHelper;
import lombok.extern.log4j.Log4j2;

/**
 * @Desc
 * @Date 2020/10/24 17:50
 * @Author AD
 */
@Log4j2
public class ProxyClient extends Proxy {
    
    public ProxyClient(ProxyConfig config) {
        super(config, false,null);
    }
    
    public void connectSuccess(Replier replier) {
        ChannelData cd = ChannelData.generate(config.getName(), replier, MessageType.ClientToServer);
        cd.setCommand(Command.ConnectSuccess);
        cd.setData(ArrayHelper.EMPTY_BYTE);
        try {
            sender.send(cd);
        } catch (Exception e) {
            log.error(StringHelper.getExceptionTrace(e));
        }
    }
    
    public void connectFailed(Replier replier) {
        ChannelData cd = ChannelData.generate(config.getName(), replier, MessageType.ClientToServer);
        cd.setCommand(Command.ConnectFailed);
        cd.setData(ArrayHelper.EMPTY_BYTE);
        try {
            sender.send(cd);
        } catch (Exception e) {
            log.error(StringHelper.getExceptionTrace(e));
        }
    }
    
    @Override
    public void start() throws Exception {
        sender.start();
    }
    
    @Override
    public void stop() throws Exception {
        sender.stop();
    }
    
}
