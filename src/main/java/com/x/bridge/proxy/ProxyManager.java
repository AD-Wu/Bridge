package com.x.bridge.proxy;

import com.x.bridge.common.ISender;
import com.x.bridge.data.ChannelData;
import com.x.bridge.data.ProxyConfig;
import com.x.bridge.proxy.client.ProxyClient;
import com.x.bridge.proxy.server.ProxyServer;
import lombok.extern.log4j.Log4j2;
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
public class ProxyManager {
    
    private static Map<String, ProxyServer> servers = new ConcurrentHashMap<>();
    private static Map<String, ProxyClient> clients = new ConcurrentHashMap<>();
    
    // 测试调用
    public void startProxyServer(ProxyConfig config, ISender<ChannelData> sender) throws Exception {
        ProxyServer server = new ProxyServer(config, sender);
        server.start();
        servers.put(config.getName(), server);
    }
    
    public void stopProxyServer(ProxyConfig config) throws Exception{
        ProxyServer server = servers.remove(config.getName());
        if(server!=null){
            server.stop();
        }
    }
    
    // 测试调用
    public void startProxyClient(ProxyConfig config,ISender<ChannelData> sender) throws Exception {
        ProxyClient client = new ProxyClient(config, sender);
        client.start();
        clients.put(config.getName(), client);
    }
    
    public void stopProxyClient(ProxyConfig config) throws Exception{
        ProxyClient client = clients.remove(config.getName());
        if(client!=null){
            client.stop();
        }
    }

    
}
