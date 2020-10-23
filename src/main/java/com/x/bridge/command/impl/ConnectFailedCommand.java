package com.x.bridge.command.impl;

import com.x.bridge.command.core.BaseCommand;
import com.x.bridge.data.ChannelData;
import com.x.bridge.proxy.server.Proxy;

public class ConnectFailedCommand extends BaseCommand {

    public ConnectFailedCommand(Proxy proxy) {
        super(proxy);
    }

    @Override
    public void execute(ChannelData cd) {

    }
}
