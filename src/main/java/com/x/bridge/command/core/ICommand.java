package com.x.bridge.command.core;

import com.x.bridge.data.ChannelData;

public interface ICommand {
    void execute(ChannelData cd);
}
