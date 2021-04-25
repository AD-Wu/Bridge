package com.x.bridge.proxy.core;

import com.x.bridge.proxy.data.ChannelData;
import com.x.bridge.proxy.data.MessageType;
import com.x.bridge.proxy.data.ProxyConfig;
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
        super(config, false);
    }
    
    public void connectSuccess(Replier replier) {
        ChannelData cd = new ChannelData();
        cd.setProxyName(config.getName());
        cd.setAppSocketClient(replier.getAppSocketClient());
        cd.setSeq(replier.getRecvSeq());
        cd.setProxyAddress(replier.getChannelInfo().getLocalAddress());
        cd.setTargetAddress(replier.getChannelInfo().getRemoteAddress());
        cd.setCommandCode(Command.ConnectSuccess.getCode());
        cd.setMessageTypeCode(MessageType.ClientToServer.getCode());
        cd.setData(ArrayHelper.EMPTY_BYTE);
        
        try {
            bridge.send(cd);
        } catch (Exception e) {
            log.error(StringHelper.getExceptionTrace(e));
        }
    }
    
    public void connectFailed(Replier replier) {
        ChannelData cd = new ChannelData();
        cd.setProxyName(config.getName());
        cd.setAppSocketClient(replier.getAppSocketClient());
        cd.setSeq(replier.getRecvSeq());
        cd.setProxyAddress(replier.getChannelInfo().getLocalAddress());
        cd.setTargetAddress(replier.getChannelInfo().getRemoteAddress());
        cd.setMessageTypeCode(MessageType.ClientToServer.getCode());
        cd.setCommandCode(Command.ConnectFailed.getCode());
        cd.setData(ArrayHelper.EMPTY_BYTE);
        try {
            bridge.send(cd);
        } catch (Exception e) {
            log.error(StringHelper.getExceptionTrace(e));
        }
    }
    
    @Override
    public void start() throws Exception {
    
    }
    
    @Override
    public void stop() throws Exception {
    
    }
    
}
