package com.x.bridge.proxy;

import com.x.bridge.proxy.core.Proxy;
import com.x.bridge.proxy.core.ProxyClient;
import com.x.bridge.proxy.core.ProxyServer;
import com.x.bridge.proxy.data.ChannelData;
import com.x.bridge.proxy.data.MessageType;
import com.x.bridge.proxy.data.ProxyConfig;
import com.x.bridge.proxy.data.ProxyConfigs;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Desc
 * @Date 2020/10/22 01:26
 * @Author AD
 */
@Log4j2
@Component
public final class ProxyManager implements InitializingBean {
    
    private static Map<String, ProxyServer> servers = new ConcurrentHashMap<>();
    
    private static Map<String, ProxyClient> clients = new ConcurrentHashMap<>();
    
    private ProxyManager() {}

    @Autowired
    private ProxyConfigs configs;

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println(configs);

    }
    
    public static void startProxy(ProxyConfig config) throws Exception {
        
        ProxyServer server = new ProxyServer(config);
        server.start();
        servers.put(config.getName(), server);
        
        ProxyClient client = new ProxyClient(config);
        client.start();
        clients.put(config.getName(), client);
        
    }
    
    public static void stopProxyServer(String proxyName) throws Exception {
        ProxyServer server = servers.remove(proxyName);
        if (server != null) {
            server.stop();
        }
    }
    
    public static void receiveData(ChannelData cd) {
        MessageType type = cd.getMessageType();
        Proxy proxy = null;
        switch (type) {
            case ClientToServer:
                proxy = getProxyServer(cd.getProxyName());
                log.info("网关收到数据 >>> 消息类型:[{}]，指令:[{}]，客户端:[{}]，代理(服务端):[{}]，服务端:[{}]，序号:[{}]，数据长度:[{}]",
                        cd.getMessageType(), cd.getCommand(), cd.getAppSocketClient(), cd.getProxyAddress(),
                        cd.getTargetAddress(), cd.getRecvSeq(), cd.getData().length);
                proxy.receive(cd);
                break;
            case ServerToClient:
                proxy = getProxyClient(cd.getProxyName());
                log.info("网关收到数据 >>> 消息类型:[{}]，指令:[{}]，客户端:[{}]，代理(客户端):[{}]，服务端:[{}]，序号:[{}]，数据长度:[{}]",
                        cd.getMessageType(), cd.getCommand(), cd.getAppSocketClient(), cd.getProxyAddress(),
                        cd.getTargetAddress(), cd.getRecvSeq(), cd.getData().length);
                proxy.receive(cd);
            default:
                break;
        }
        if (proxy == null) {
            log.error("网关中没有该代理:[{}]，通道数据:{}", cd.getProxyName(), cd);
        }
        
    }
    
    public static ProxyServer getProxyServer(String proxyName) {
        return servers.get(proxyName);
    }
    
    public static ProxyClient getProxyClient(String proxyName) {
        return clients.get(proxyName);
    }
    
    public static void addProxyClient(String proxyName, ProxyClient client) {
        clients.put(proxyName, client);
    }

}
