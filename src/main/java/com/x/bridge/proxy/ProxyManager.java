package com.x.bridge.proxy;

import com.x.bridge.proxy.command.core.Command;
import com.x.bridge.proxy.command.core.Commands;
import com.x.bridge.proxy.command.core.ICommand;
import com.x.bridge.proxy.core.Proxy;
import com.x.bridge.proxy.core.ProxyClient;
import com.x.bridge.proxy.core.ProxyServer;
import com.x.bridge.proxy.data.ChannelData;
import com.x.bridge.proxy.data.MessageType;
import com.x.bridge.proxy.data.ProxyConfig;
import com.x.bridge.proxy.data.ProxyConfigs;
import com.x.doraemon.util.Strings;
import lombok.extern.log4j.Log4j2;
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
public final class ProxyManager {
    
    private static Map<String, ProxyServer> servers = new ConcurrentHashMap<>();
    
    private static Map<String, ProxyClient> clients = new ConcurrentHashMap<>();
    
    private ProxyManager() {
    }
    
    @Autowired
    private ProxyConfigs configs;
    
    public static void startProxy(String name) throws Exception {
        ProxyConfig config = ProxyConfigs.get(name);
        if (config != null) {
            startProxy(config);
        } else {
            log.error("不存在名为:[{}]的配置", name);
        }
    }
    
    public static void startProxy(ProxyConfig config) throws Exception {
        String proxyAddress = config.getProxyAddress();
        if (Strings.isNotNull(proxyAddress)) {
            ProxyServer server = new ProxyServer(config);
            server.start();
            servers.put(config.getName(), server);
        }
        // ProxyClient client = new ProxyClient(config);
        // client.start();
        // clients.put(config.getName(), client);
        
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
                if (proxy == null) {
                    if (cd.getCommand() == Command.ConnectRequest) {
                        ICommand command = Commands.getCommand(Command.ConnectRequest.getCmd());
                        try {
                            command.execute(cd);
                        } catch (Exception e) {
                            log.error(Strings.getExceptionTrace(e));
                        }
                    } else {
                        log.error("丢包");
                    }
                } else {
                    proxy.receive(cd);
                }
            
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
    
    // ------------------------ 变量定义 ------------------------
    // ------------------------ 构造方法 ------------------------
    // ------------------------ 方法定义 ------------------------
    // ------------------------ 私有方法 ------------------------
    private void autoCreateMethod() {}
    
}
