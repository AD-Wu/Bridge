package com.x.bridge.command.impl;

import com.x.bridge.command.core.BaseCommand;
import com.x.bridge.data.ChannelData;
import com.x.bridge.proxy.server.Proxy;

public class DisconnectCommand extends BaseCommand {

    public DisconnectCommand(Proxy proxy) {
        super(proxy);
    }

    @Override
    public void execute(ChannelData cd) {

    }
}
