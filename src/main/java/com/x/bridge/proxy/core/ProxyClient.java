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
 public class ProxyClient extends Proxy{
    
    public ProxyClient(ProxyConfig config) {
        super(config, false);
    }
    
    public void connectSuccess(Replier replier) {
        ChannelData cd = ChannelData.builder()
                .proxyName(config.getName())
                .appSocketClient(replier.getAppSocketClient())
                .seq(replier.getRecvSeq())
                .proxyAddress(replier.getChannelInfo().getLocalAddress())
                .targetAddress(replier.getChannelInfo().getRemoteAddress())
                .commandCode(Command.ConnectSuccess.getCode())
                .messageTypeCode(MessageType.ClientToServer.getCode())
                .data(ArrayHelper.EMPTY_BYTE)
                .build();
        try {
            bridge.send(cd);
        } catch (Exception e) {
            log.error(StringHelper.getExceptionTrace(e));
        }
    }
    
    public void connectFailed(Replier replier) {
        ChannelData cd = ChannelData.builder()
                .proxyName(config.getName())
                .appSocketClient(replier.getAppSocketClient())
                .seq(replier.getRecvSeq())
                .proxyAddress(replier.getChannelInfo().getLocalAddress())
                .targetAddress(replier.getChannelInfo().getRemoteAddress())
                .messageTypeCode(MessageType.ClientToServer.getCode())
                .commandCode(Command.ConnectFailed.getCode())
                .data(ArrayHelper.EMPTY_BYTE)
                .build();
        try {
            bridge.send(cd);
        } catch (Exception e) {
            log.error(StringHelper.getExceptionTrace(e));
        }
    }
   
}
