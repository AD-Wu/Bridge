package com.x.bridge.proxy.client;

import com.x.bridge.common.ISender;
import com.x.bridge.data.ChannelData;
import com.x.bridge.data.ProxyConfig;
import com.x.bridge.proxy.core.Command;
import com.x.bridge.proxy.core.Proxy;
import com.x.doraemon.util.ArrayHelper;
import lombok.extern.log4j.Log4j2;

/**
 * @Desc
 * @Date 2020/10/24 17:50
 * @Author AD
 */
@Log4j2
public class ProxyClient extends Proxy<ChannelData> {
    
    public ProxyClient(ProxyConfig config, ISender<ChannelData> sender) {
        super(config, sender);
    }
    
    @Override
    public String name() {
        return config.getName();
    }
    
    @Override
    public void start() throws Exception {
    }
    
    @Override
    public void stop() throws Exception {
    }
    
    @Override
    public void onReceive(ChannelData... datas) {
        if (!ArrayHelper.isEmpty(datas)) {
            for (ChannelData data : datas) {
                Command command = data.getCommand();
                if (command != null) {
                    try {
                        command.getActor().execute(this, data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
}
