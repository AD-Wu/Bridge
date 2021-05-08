package com.x.bridge.proxy.command;

import com.x.bridge.common.IFactory;
import com.x.bridge.data.ChannelData;
import com.x.bridge.proxy.command.core.ICommand;
import com.x.bridge.proxy.core.Command;
import com.x.bridge.proxy.core.Proxy;
import com.x.bridge.proxy.core.Replier;
import com.x.doraemon.util.ArrayHelper;
import lombok.extern.log4j.Log4j2;

/**
 * @Desc
 * @Date 2021/5/3 19:24
 * @Author AD
 */
@Log4j2
public class Disconnect implements ICommand<ChannelData>, IFactory<Replier, ChannelData> {

    @Override
    public void send(Proxy<ChannelData> proxy, ChannelData data) {
        proxy.send(data);
    }

    @Override
    public void execute(Proxy<ChannelData> proxy, ChannelData cd) {
        // 获取应用客户端地址
        String appSocket = cd.getAppClient();
        // 移除应答者
        Replier replier = proxy.removeReplier(appSocket);
        // 关闭通道
        if (replier != null) {
            // 日志记录
            log.info("连接关闭:[{}]", appSocket);
            replier.setConnected(false);
            replier.close();
        }
    }

    @Override
    public ChannelData get(Replier replier) {
        ChannelData cd = ChannelData.generate(replier);
        cd.setCommand(Command.Disconnect);
        cd.setData(ArrayHelper.EMPTY_BYTE);
        return cd;
    }

}
