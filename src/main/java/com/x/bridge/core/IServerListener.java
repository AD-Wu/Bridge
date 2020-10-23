package com.x.bridge.core;

public interface IServerListener extends ISocketListener{

    void onServerStart(SocketConfig config);

    void onServerStartError(Throwable e);

    void onServerStop(SocketConfig config);
}
