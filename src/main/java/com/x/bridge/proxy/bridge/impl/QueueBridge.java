package com.x.bridge.proxy.bridge.impl;

import com.google.auto.service.AutoService;
import com.x.bridge.proxy.ProxyManager;
import com.x.bridge.proxy.bridge.core.BaseBridge;
import com.x.bridge.proxy.bridge.core.IBridge;
import com.x.bridge.proxy.data.ChannelData;
import com.x.bridge.proxy.data.MessageType;
import com.x.doraemon.util.Strings;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Desc 队列桥
 * @Date 2020/10/25 12:10
 * @Author AD
 */
@Log4j2
@AutoService(IBridge.class)
public class QueueBridge extends BaseBridge {
    
    private final LinkedBlockingQueue<ChannelData> serverToClient;
    
    private final LinkedBlockingQueue<ChannelData> clientToServer;
    
    private final ExecutorService runner;
    
    public QueueBridge() {
        this.serverToClient = new LinkedBlockingQueue();
        this.clientToServer = new LinkedBlockingQueue<>();
        this.runner = Executors.newFixedThreadPool(2);
    }
    
    @Override
    public String name() {
        return "queue";
    }
    
    @Override
    public void send(ChannelData cd) throws Exception {
        if (MessageType.ServerToClient == cd.getMessageType()) {
            log.info("队列发送数据 >>> 消息类型:[{}]，指令:[{}]，客户端:[{}]，代理(服务端):[{}]，服务端:[{}]，序号:[{}]，数据长度:[{}]",
                    cd.getMessageType(), cd.getCommand(), cd.getAppSocketClient(), cd.getProxyAddress(),
                    cd.getTargetAddress(), cd.getRecvSeq(), cd.getData().length);
            serverToClient.add(cd);
        } else {
            log.info("队列发送数据 >>> 消息类型:[{}]，指令:[{}]，客户端:[{}]，代理(客户端):[{}]，服务端:[{}]，序号:[{}]，数据长度:[{}]",
                    cd.getMessageType(), cd.getCommand(), cd.getAppSocketClient(), cd.getProxyAddress(),
                    cd.getTargetAddress(), cd.getRecvSeq(), cd.getData().length);
            clientToServer.add(cd);
        }
    }
    
    @Override
    public IBridge newInstance() {
        return new QueueBridge();
    }
    
    @Override
    public void onStart() throws Exception {
        runner.execute(() -> {
            while (true) {
                try {
                    ChannelData cd = serverToClient.take();
                    ProxyManager.receiveData(cd);
                } catch (InterruptedException e) {
                    log.error(Strings.getExceptionTrace(e));
                }
            }
        });
        runner.execute(() -> {
            while (true) {
                try {
                    ChannelData cd = clientToServer.take();
                    ProxyManager.receiveData(cd);
                } catch (InterruptedException e) {
                    log.error(Strings.getExceptionTrace(e));
                }
            }
        });
    }
    
    @Override
    public void onStop() throws Exception {
        runner.shutdown();
        serverToClient.clear();
        clientToServer.clear();
    }
    
}
