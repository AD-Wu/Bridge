package com.x.bridge.proxy.command;

import com.x.bridge.data.ChannelData;
import com.x.bridge.proxy.command.core.ICommand;
import com.x.bridge.proxy.core.Proxy;
import com.x.bridge.proxy.core.Replier;

/**
 * @Desc
 * @Date 2021/5/3 19:20
 * @Author AD
 */
public class ConnectSuccess implements ICommand<ChannelData> {
    
    @Override
    public void execute(Proxy proxy, ChannelData cd) throws Exception {
        // 获取应用客户端地址
        String appSocket = cd.getAppSocketClient();
        // 获取应答者
        Replier replier = proxy.getReplier(appSocket);
        if (replier != null) {
            // 获取应答者连接建立锁
            Object connectLock = replier.getConnectLock();
            // 通知连接建立成功
            synchronized (connectLock) {
                replier.setConnected(true);
                replier.setConnectTimeout(false);
                replier.setProxyClient(cd.getProxyClient());
                connectLock.notify();
            }
        }
    }
    
}
