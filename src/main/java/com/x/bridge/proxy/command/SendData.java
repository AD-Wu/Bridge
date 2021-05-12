package com.x.bridge.proxy.command;

import com.x.bridge.data.ChannelData;
import com.x.bridge.proxy.command.core.ICommand;
import com.x.bridge.proxy.core.Command;
import com.x.bridge.proxy.core.Proxy;
import com.x.bridge.proxy.core.Replier;

/**
 * @Desc
 * @Date 2021/5/3 19:25
 * @Author AD
 */
public class SendData implements ICommand<ChannelData> {

    @Override
    public void receive(Proxy<ChannelData> proxy, ChannelData cd) {
        // 获取应用客户端地址
        String appSocket = cd.getAppClient();
        // 获取应答者
        Replier replier = proxy.getReplier(appSocket);
        if (replier != null) {
            // 获取发送数据
            replier.send(cd.getRecvSeq(), cd.getData());
        }
    }

    public static ChannelData getData(Replier replier, byte[] data) {
        ChannelData cd = ChannelData.generate(replier);
        cd.setCommand(Command.SendData);
        cd.setData(data);
        return cd;
    }

}
