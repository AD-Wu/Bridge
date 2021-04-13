package com.x.bridge.proxy;

import com.x.bridge.proxy.command.core.Command;
import com.x.bridge.proxy.core.Proxy;
import com.x.bridge.proxy.core.ProxyClient;
import com.x.bridge.proxy.core.ProxyServer;
import com.x.bridge.proxy.data.ChannelData;
import com.x.bridge.proxy.data.MessageType;
import com.x.bridge.proxy.data.ProxyConfig;
import com.x.bridge.proxy.data.ProxyConfigManager;
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
    private ProxyConfigManager configs;

    // web调用
    public static void startProxyServer(String proxyName) throws Exception {
        ProxyConfig config = ProxyConfigManager.get(proxyName);
        if (config != null) {
            startProxyServer(config);
        } else {
            log.error("不存在名为:[{}]的配置", proxyName);
        }
    }

    // 测试调用
    public static void startProxyServer(ProxyConfig config) throws Exception {
        String proxyAddress = config.getProxyAddress();
        if (Strings.isNotNull(proxyAddress)) {
            ProxyServer server = new ProxyServer(config);
            server.start();
            servers.put(config.getName(), server);
        }
    }

    public static void stopProxyServer(String proxyName) throws Exception {
        ProxyServer server = servers.remove(proxyName);
        if (server != null) {
            server.stop();
        }
    }

    public static void startProxyClient(String proxyName) throws Exception {
        ProxyConfig config = ProxyConfigManager.get(proxyName);
        if (config != null) {
            startProxyClient(config);
        } else {
            log.error("不存在名为:[{}]的配置", proxyName);
        }
    }

    // 测试调用
    public static void startProxyClient(ProxyConfig config) throws Exception {
        ProxyClient client = new ProxyClient(config);
        client.start();
        clients.put(config.getName(), client);
    }

    public static void stopProxyClient(String proxyName) throws Exception {
        ProxyClient client = clients.remove(proxyName);
        if (client != null) {
            client.stop();
        }
    }

    public static void receiveData(ChannelData cd) {
        MessageType type = cd.getMessageType();
        Proxy proxy = null;
        switch (type) {
            case ClientToServer:
                log(cd);
                proxy = getProxyServer(cd.getProxyName());
                if (proxy != null) {
                    proxy.receive(cd);
                }
                break;
            case ServerToClient:
                log(cd);
                proxy = getProxyClient(cd.getProxyName());
                if (proxy != null) {
                    proxy.receive(cd);
                }
                break;
            default:
                break;
        }
        if (proxy == null && cd.getCommand() != Command.ConnectRequest) {
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

    private static void log(ChannelData cd) {
        log.info("网关收到数据 >>> 消息类型:[{}]，指令:[{}]，客户端:[{}]，代理(客户端):[{}]，服务端:[{}]，序号:[{}]，数据长度:[{}]",
                cd.getMessageType(), cd.getCommand(), cd.getAppSocketClient(), cd.getProxyAddress(),
                cd.getTargetAddress(), cd.getRecvSeq(), cd.getData().length);
    }

}
