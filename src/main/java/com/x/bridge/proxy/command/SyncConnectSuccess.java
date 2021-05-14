package com.x.bridge.proxy.command;

import com.x.bridge.data.ChannelData;
import com.x.bridge.proxy.command.core.ICommand;
import com.x.bridge.proxy.core.Command;
import com.x.bridge.proxy.core.Proxy;
import com.x.bridge.proxy.core.Replier;
import com.x.doraemon.util.ArrayHelper;
import lombok.extern.log4j.Log4j2;

/**
 * @Desc
 * @Date 2021/5/12 16:51
 * @Author AD
 */
@Log4j2
public class SyncConnectSuccess implements ICommand<ChannelData> {

    @Override
    public void receive(Proxy<ChannelData> proxy, ChannelData cd) {
        // 获取应答者
        Replier replier = proxy.getReplier(cd.getAppClient());
        if (replier != null) {
            // 通知连接建立成功
            synchronized (replier.getConnectLock()) {
                replier.setConnected(true);
                replier.setConnectTimeout(false);
                replier.setProxyClient(cd.getProxyClient());
                replier.getConnectLock().notifyAll();
            }
            log.info("连接建立成功，客户端:[{}]，代理(服务端):[{}]，服务端:[{}]", cd.getAppClient(), cd.getProxyServer(), cd.getAppServer());
        }
    }


    public static ChannelData getData(Replier replier) {
        ChannelData cd = ChannelData.generate(replier);
        cd.setCommand(Command.SyncConnectSuccess);
        cd.setData(ArrayHelper.EMPTY_BYTE);
        return cd;
    }

}
