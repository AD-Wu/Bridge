package com.x.bridge.proxy.core;

import com.x.bridge.core.SocketConfig;
import com.x.bridge.core.SocketServer;
import com.x.bridge.proxy.command.core.Command;
import com.x.bridge.proxy.data.ChannelData;
import com.x.bridge.proxy.data.MessageType;
import com.x.bridge.proxy.data.ProxyConfig;
import com.x.bridge.proxy.util.ProxyHelper;
import com.x.doraemon.util.ArrayHelper;
import com.x.doraemon.util.Strings;
import lombok.extern.log4j.Log4j2;

/**
 * @Desc
 * @Date 2020/10/24 17:35
 * @Author AD
 */
@Log4j2
public class ProxyServer extends Proxy  {
    
    private final SocketServer server;
    
    public ProxyServer(ProxyConfig config) {
        super(config, true);
        int port = ProxyHelper.getPort(config.getProxyAddress());
        this.server = new SocketServer(new SocketConfig(port), new ServerListener(this));
    }
    
    @Override
    public void proxyStart() throws Exception {
        runner.execute(server);
    }
    
    @Override
    public void proxyStop() throws Exception {
        runner.execute(()->{
            server.stop();
            runner.shutdown();
            for (Replier replier : repliers.values()) {
                replier.close();
            }
            repliers.clear();
        });
        
    }
    
    public boolean isAccept(String socket){
        return config.isAllowClient(socket);
    }
    
    public boolean connectRequest(Replier replier) {
        ChannelData cd = ChannelData.builder()
                .proxyName(config.getName())
                .appSocketClient(replier.getAppSocketClient())
                .recvSeq(replier.getRecvSeq())
                .proxyAddress(replier.getChannelInfo().getLocalAddress())
                .targetAddress(config.getTargetAddress())
                .messageType(MessageType.ServerToClient)
                .command(Command.ConnectRequest)
                .data(ArrayHelper.EMPTY_BYTE)
                .build();
        Object lock = replier.getConnectLock();
        try {
            synchronized (lock) {
                bridge.send(cd);
                lock.wait(config.getConnectTimeout() * 1000);
            }
            if (replier.isConnectTimeout()) {
                log.info("连接超时，配置时间:{}秒，连接关闭",config.getConnectTimeout());
                return false;
            }
            return replier.isConnected();
        } catch (Exception e) {
            log.error(Strings.getExceptionTrace(e));
            return false;
        }
    }
    
}