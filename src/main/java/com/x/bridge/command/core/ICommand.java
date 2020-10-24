package com.x.bridge.command.core;

import com.x.bridge.proxy.data.ChannelData;

public interface ICommand {
    void execute(ChannelData cd)throws Exception;
}
