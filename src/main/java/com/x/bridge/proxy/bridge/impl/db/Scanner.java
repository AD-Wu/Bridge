package com.x.bridge.proxy.bridge.impl.db;

import com.x.bridge.proxy.data.ChannelData;

import java.util.concurrent.Callable;

public class Scanner implements Callable<ChannelData[]> {


    private final String table;

    public Scanner(String table) {
        this.table = table;
    }

    @Override
    public ChannelData[] call() throws Exception {
        return null;
    }

}
