package com.x.bridge.command.core;

import com.x.bridge.proxy.server.Proxy;
import com.x.doraemon.util.Logs;
import lombok.Data;
import org.apache.log4j.Logger;

@Data
public abstract  class BaseCommand implements ICommand {

    protected final Proxy proxy;

    protected final Logger log = Logs.getLogger(this.getClass());

    public BaseCommand(Proxy proxy) {
        this.proxy = proxy;
    }

}
