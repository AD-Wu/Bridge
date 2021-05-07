package com.x.bridge.proxy.command;

import com.x.bridge.common.ISender;
import com.x.bridge.data.ChannelData;
import com.x.bridge.proxy.command.core.ICommand;
import com.x.bridge.proxy.core.MessageType;
import com.x.bridge.proxy.core.Proxy;
import com.x.bridge.proxy.core.Replier;
import lombok.extern.log4j.Log4j2;

/**
 * @Desc
 * @Date 2021/5/3 19:24
 * @Author AD
 */
@Log4j2
public class Disconnect implements ICommand<ChannelData> {
    
    @Override
    public void request(Proxy<ChannelData> proxy, ChannelData data) {
        ISender<ChannelData> sender = proxy.getSender();
        try {
            sender.send(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void response(Proxy<ChannelData> proxy, ChannelData cd) {
        // 获取应用客户端地址
        String appSocket = cd.getAppSocketClient();
        String sc = cd.getMessageType() == MessageType.ServerToClient ? "客户端" : "服务端";
        // 移除应答者
        Replier replier = proxy.removeReplier(appSocket);
        // 关闭通道
        if (replier != null) {
            // 日志记录
            log.info("连接关闭，客户端:[{}]，代理({}):[{}]，服务端:[{}]",
                    appSocket, sc, replier.getChannelInfo().getLocalAddress(),
                    replier.getChannelInfo().getRemoteAddress());
            replier.setConnected(false);
            replier.close();
        }
    }
    
}