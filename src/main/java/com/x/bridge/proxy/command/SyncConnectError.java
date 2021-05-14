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
 * @Date 2021/5/12 16:54
 * @Author AD
 */
@Log4j2
public class SyncConnectError implements ICommand<ChannelData> {

    @Override
    public void receive(Proxy<ChannelData> proxy, ChannelData cd) {
        // 获取应答者
        Replier replier = proxy.getReplier(cd.getAppClient());
        if (replier != null) {
            // 通知连接建立失败
            synchronized (replier.getConnectLock()) {
                replier.setConnected(false);
                replier.setConnectTimeout(false);
                replier.close();
                replier.getConnectLock().notifyAll();
            }
            log.info("连接建立失败，客户端:[{}]，代理(服务端):[{}]，服务端:[{}]，失败原因:{}",
                    cd.getAppClient(), cd.getProxyServer(), cd.getAppServer(), cd.getException());
        }
    }


    public static ChannelData getData(Replier replier, String exception) {
        ChannelData cd = ChannelData.generate(replier);
        cd.setCommand(Command.SyncConnectError);
        cd.setData(ArrayHelper.EMPTY_BYTE);
        cd.setException(exception);
        return cd;
    }

}
