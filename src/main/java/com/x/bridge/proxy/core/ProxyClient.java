package com.x.bridge.proxy.core;

import com.x.bridge.command.core.Command;
import com.x.bridge.proxy.MessageType;
import com.x.bridge.proxy.data.ChannelData;
import com.x.bridge.proxy.data.ProxyConfig;
import com.x.doraemon.util.ArrayHelper;
import com.x.doraemon.util.Strings;
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
        ChannelData cd = ChannelData.builder()
                .proxyName(config.getName())
                .appSocketClient(replier.getAppSocketClient())
                .recvSeq(replier.getRecvSeq())
                .targetAddress(replier.getChannelInfo().getRemoteAddress())
                .command(Command.ConnectSuccess.getCmd())
                .messageType(MessageType.ClientToServer.getCode())
                .data(ArrayHelper.EMPTY_BYTE)
                .build();
        try {
            bridge.send(cd);
        } catch (Exception e) {
            log.error(Strings.getExceptionTrace(e));
        }
    }
    
    public void connectFailed(Replier replier) {
        ChannelData cd = ChannelData.builder()
                .proxyName(config.getName())
                .appSocketClient(replier.getAppSocketClient())
                .recvSeq(replier.getRecvSeq())
                .targetAddress(replier.getChannelInfo().getRemoteAddress())
                .messageType(MessageType.ClientToServer.getCode())
                .command(Command.ConnectFailed.getCmd())
                .data(ArrayHelper.EMPTY_BYTE)
                .build();
        try {
            bridge.send(cd);
        } catch (Exception e) {
            log.error(Strings.getExceptionTrace(e));
        }
    }
    
    @Override
    protected void proxyStart() throws Exception {
    
    }
    
    @Override
    protected void proxyStop() throws Exception {
    
    }
   
}
