package com.x.bridge.core;

import com.x.bridge.data.ReplierManager;
import com.x.bridge.proxy.client.ProxyClientListener;
import com.x.bridge.proxy.server.ProxyServerListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Test {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService threads = Executors.newCachedThreadPool();
        ReplierManager replierManager = new ReplierManager(1111);
        SocketServer server = new SocketServer(SocketConfig.serverConfig(1111),new ProxyServerListener(replierManager));
        threads.execute(server);

        SocketClient client = new SocketClient(SocketConfig.clientConfig("localhost", 1111), new ProxyClientListener("localhost", replierManager));
        threads.execute(client);

        TimeUnit.SECONDS.sleep(3);
        server.stop();
        client.disconnect();

        System.out.println("非阻塞");

        Thread.currentThread().join();
    }
}
