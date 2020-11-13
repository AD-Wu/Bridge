package com.x.bridge.proxy.bridge.impl;

import com.google.auto.service.AutoService;
import com.x.bridge.proxy.bridge.core.BaseBridge;
import com.x.bridge.proxy.bridge.core.IBridge;
import com.x.bridge.proxy.data.ChannelData;
import lombok.extern.log4j.Log4j2;

/**
 * @Desc 数据库桥
 * @Date 2020/11/13 17:36
 * @Author AD
 */
@Log4j2
@AutoService(IBridge.class)
public class DBBridge extends BaseBridge {


    @Override
    protected void onStart() throws Exception {

    }

    @Override
    protected void onStop() throws Exception {

    }

    @Override
    public String name() {
        return "DB";
    }

    @Override
    public void send(ChannelData data) throws Exception {

    }

    @Override
    public IBridge<ChannelData> newInstance() {
        return new DBBridge();
    }

}
