package com.x.bridge.proxy.server;

import com.x.bridge.command.core.Command;
import com.x.bridge.core.SocketConfig;
import com.x.bridge.core.SocketServer;
import com.x.bridge.data.ChannelData;
import com.x.bridge.data.IBridge;
import com.x.bridge.data.ProxyConfig;
import com.x.bridge.data.ReplierManager;
import com.x.bridge.proxy.ProxyConfigManager;
import com.x.bridge.proxy.Replier;
import com.x.bridge.util.AppHelper;
import com.x.doraemon.util.ArrayHelper;
import com.x.doraemon.util.Strings;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Desc 代理对象
 * @Date 2020/10/22 01:07
 * @Author AD
 */
@Log4j2
@Data
public class Proxy {

    private final SocketServer server;

    private final ReplierManager replierManager;

    private final ProxyConfig config;

    private final IBridge bridge;
    
    private final ExecutorService runner;

    public Proxy(String proxyAddress, IBridge bridge) {
        this.bridge = bridge;
        this.runner = Executors.newCachedThreadPool();
        this.config = ProxyConfigManager.getProxyConfig(proxyAddress);
        this.replierManager = new ReplierManager(proxyAddress);
        this.server = new SocketServer(new SocketConfig(AppHelper.getPort(proxyAddress)),
                new ServerListener(replierManager));
    }

     public boolean connectRequest(Replier replier) {
        ChannelData cd = new ChannelData();
        cd.setAppSocketClient(replier.getChannelInfo().getRemoteAddress());
        cd.setRecvSeq(replier.getRecvSeq());
        cd.setTargetAddress(config.getTargetAddress());
        cd.setCmd(Command.ConnectRequest);
        cd.setData(ArrayHelper.EMPTY_BYTE);

        Object lock = replier.getConnectLock();
        try {
            synchronized (lock) {
                bridge.send(cd);
                lock.wait(config.getConnectTimeout() * 1000);
            }
            if (replier.isConnectTimeout()) {
                log.info("连接超时，配置时间:{}秒，连接关闭");
                return false;
            }
            return replier.isConnected();
        } catch (Exception e) {
            log.error(Strings.getExceptionTrace(e));
            return false;
        }
    }

    public void connectSuccess(Replier replier){

    }

    public void disconnect(Replier replier) {
        ChannelData cd = new ChannelData();
        cd.setAppSocketClient(replier.getChannelInfo().getRemoteAddress());
        cd.setRecvSeq(replier.getRecvSeq());
        cd.setTargetAddress(config.getTargetAddress());
        cd.setCmd(Command.Disconnect);
        cd.setData(ArrayHelper.EMPTY_BYTE);
        try {
            bridge.send(cd);
        } catch (Exception e) {
            log.error(Strings.getExceptionTrace(e));
        }
    }

    public void sendToProxy(String appSocketClient, long seq, byte[] data) {
        ChannelData cd = new ChannelData();
        cd.setAppSocketClient(appSocketClient);
        cd.setRecvSeq(seq);
        cd.setTargetAddress(config.getTargetAddress());
        cd.setCmd(Command.SendData);
        cd.setData(data);
        try {
            bridge.send(cd);
        } catch (Exception e) {
            log.error(Strings.getExceptionTrace(e));
        }
    }

    public void recvFromProxy(ChannelData cd){
        String remote = cd.getAppSocketClient();
        Command cmd = cd.getCmd();
        byte[] data = cd.getData();
        long seq = cd.getRecvSeq();
        Replier replier = replierManager.getReplier(remote);
        if(replier!=null){

        }
    }

}
