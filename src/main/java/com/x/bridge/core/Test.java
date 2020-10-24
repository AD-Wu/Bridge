package com.x.bridge.core;

import com.x.bridge.proxy.ReplierManager;
import com.x.bridge.proxy.client.ClientListener;
import com.x.bridge.proxy.server.ServerListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Test {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService threads = Executors.newCachedThreadPool();
        ReplierManager replierManager = new ReplierManager("localhost:1111");
        SocketServer server = new SocketServer(new SocketConfig(1111),new ServerListener(replierManager));
        threads.execute(server);

        TimeUnit.SECONDS.sleep(2);
        SocketClient client = new SocketClient(new SocketConfig("localhost", 1111),
                new ClientListener("localhost", "localhost:1111",replierManager));
        threads.execute(client);

        TimeUnit.SECONDS.sleep(3);
        server.stop();
        client.disconnect();

        System.out.println("非阻塞");

        Thread.currentThread().join();
    }
}
