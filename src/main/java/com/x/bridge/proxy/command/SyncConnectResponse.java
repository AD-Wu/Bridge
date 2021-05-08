package com.x.bridge.proxy.command;

import com.x.bridge.common.IFactory;
import com.x.bridge.data.ChannelData;
import com.x.bridge.proxy.command.core.ICommand;
import com.x.bridge.proxy.core.Command;
import com.x.bridge.proxy.core.Proxy;
import com.x.bridge.proxy.core.Replier;
import com.x.doraemon.util.ArrayHelper;
import com.x.doraemon.util.StringHelper;
import lombok.extern.log4j.Log4j2;

/**
 * @Desc
 * @Date 2021/5/7 19:57
 * @Author AD
 */
@Log4j2
public class SyncConnectResponse implements ICommand<ChannelData>, IFactory<Replier, ChannelData> {

    @Override
    public void send(Proxy<ChannelData> proxy, ChannelData cd) {
        proxy.send(cd);
    }

    @Override
    public void execute(Proxy<ChannelData> proxy, ChannelData cd) {
        // 获取应用客户端地址
        String appSocket = cd.getAppClient();
        // 获取应答者
        Replier replier = proxy.getReplier(appSocket);
        if (replier != null) {
            // 获取应答者连接建立锁
            Object connectLock = replier.getConnectLock();
            // 获取异常
            if (StringHelper.isNull(cd.getException(), "none", "null")) {
                // 通知连接建立成功
                synchronized (connectLock) {
                    replier.setConnected(true);
                    replier.setConnectTimeout(false);
                    replier.setProxyClient(cd.getProxyClient());
                    connectLock.notify();
                }
                log.info("连接建立成功，客户端:[{}]，代理(服务端):[{}]，服务端:[{}]",
                        cd.getAppClient(), cd.getProxyServer(), cd.getAppServer());
            } else {
                // 通知连接建立失败
                synchronized (connectLock) {
                    replier.setConnected(false);
                    connectLock.notify();
                }
                log.info("连接建立失败，客户端:[{}]，代理(服务端):[{}]，服务端:[{}]",
                        cd.getAppClient(), cd.getProxyServer(), cd.getAppServer());
            }

        }

    }

    @Override
    public ChannelData get(Replier replier) {
        ChannelData cd = ChannelData.generate(replier);
        cd.setCommand(Command.SyncClientConnect);
        cd.setData(ArrayHelper.EMPTY_BYTE);
        return cd;
    }

}
