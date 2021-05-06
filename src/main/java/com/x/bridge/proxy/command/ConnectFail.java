package com.x.bridge.proxy.command;

import com.x.bridge.data.ChannelData;
import com.x.bridge.proxy.command.core.ICommand;
import com.x.bridge.proxy.core.Proxy;
import com.x.bridge.proxy.core.Replier;
import lombok.extern.log4j.Log4j2;

/**
 * @Desc
 * @Date 2021/5/3 19:23
 * @Author AD
 */
@Log4j2
public class ConnectFail implements ICommand<ChannelData> {
    
    @Override
    public void execute(Proxy proxy, ChannelData cd) throws Exception {
        // 获取应用客户端地址
        String appSocket = cd.getAppSocketClient();
        // 移除应答者
        Replier replier = proxy.removeReplier(appSocket);
        if (replier != null) {
            // 获取应答者连接建立锁
            Object connectLock = replier.getConnectLock();
            // 通知连接建立成功
            synchronized (connectLock) {
                replier.setConnected(false);
                connectLock.notify();
            }
        }
        log.info("应用客户端:[{}]与目标服务器:[{}]连接建立失败", cd.getAppSocketClient(), cd.getAppSocketServer());
    }
    
}
