package com.x.bridge.proxy.command;

import com.x.bridge.common.IFactory;
import com.x.bridge.data.ChannelData;
import com.x.bridge.proxy.command.core.ICommand;
import com.x.bridge.proxy.core.Command;
import com.x.bridge.proxy.core.Proxy;
import com.x.bridge.proxy.core.Replier;
import com.x.doraemon.util.ArrayHelper;

/**
 * @Desc
 * @Date 2021/5/3 19:25
 * @Author AD
 */
public class SendData implements ICommand<ChannelData>, IFactory<Replier,ChannelData> {
    
    @Override
    public void send(Proxy<ChannelData> proxy, ChannelData data) {
        try {
            proxy.send(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void execute(Proxy<ChannelData> proxy, ChannelData cd) {
        // 获取应用客户端地址
        String appSocket = cd.getAppClient();
        // 获取应答者
        Replier replier = proxy.getReplier(appSocket);
        if (replier != null) {
            // 获取发送数据
            replier.send(cd.getSeq(), cd.getData());
        }
    }
    
    @Override
    public ChannelData get(Replier replier) {
        ChannelData cd = ChannelData.generate(replier);
        cd.setCommand(Command.SendData);
        cd.setData(ArrayHelper.EMPTY_BYTE);
        return cd;
    }
    
}
