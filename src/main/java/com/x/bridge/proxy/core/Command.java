package com.x.bridge.proxy.core;

import com.x.bridge.common.SocketClient;
import com.x.bridge.common.SocketConfig;
import com.x.bridge.proxy.ProxyManager;
import com.x.bridge.proxy.data.ChannelData;
import com.x.bridge.proxy.data.MessageType;
import com.x.bridge.proxy.data.ProxyConfig;
import com.x.bridge.proxy.data.ProxyConfigManager;
import com.x.bridge.proxy.util.ProxyHelper;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;

/**
 * @Desc
 * @Date 2020/10/22 21:00
 * @Author AD
 */
@Log4j2
public enum Command {
    ConnectRequest(1) {
        @Override
        public void execute(ChannelData cd) throws Exception {
            // 获取应用客户端地址
            String appSocket = cd.getAppSocketClient();
            // 获取代理
            ProxyClient client = ProxyManager.getProxyClient(cd.getProxyName());
            // 首次请求
            if (client == null) {
                ProxyConfig config = ProxyConfigManager.get(cd.getProxyName());
                client = new ProxyClient(config);
                client.start();
                ProxyManager.addProxyClient(cd.getProxyName(), client);
            }
            // 获取应答者
            Replier replier = client.getReplier(appSocket);
            // 未建立建立
            if (replier == null) {
                // 创建socket客户端连接目标服务器
                String ip = ProxyHelper.getIP(cd.getTargetAddress());
                int port = ProxyHelper.getPort(cd.getTargetAddress());
                SocketClient socket = new SocketClient(
                        new SocketConfig(ip, port),
                        new ProxyClientListener(appSocket, cd.getProxyAddress(), client));
                // client.getRunner().execute(socket);
            } else {
                log.info("代理服务器:[{}],连接:[{}]已存在，不再重新建立",
                        cd.getProxyAddress(), cd.getAppSocketClient());
            }
        }
    },
    ConnectSuccess(2) {
        @Override
        public void execute(ChannelData cd) throws Exception {
            // 获取应用客户端地址
            String appSocket = cd.getAppSocketClient();
            // 获取代理
            ProxyServer server = ProxyManager.getProxyServer(cd.getProxyName());
            // 获取应答者
            Replier replier = server.getReplier(appSocket);
            if (replier != null) {
                // 获取应答者连接建立锁
                Object connectLock = replier.getConnectLock();
                // 通知连接建立成功
                synchronized (connectLock) {
                    replier.setConnected(true);
                    replier.setConnectTimeout(false);
                    connectLock.notify();
                }
            }
            
        }
    },
    ConnectFailed(3) {
        @Override
        public void execute(ChannelData cd) throws Exception {
            // 获取应用客户端地址
            String appSocket = cd.getAppSocketClient();
            // 获取代理
            ProxyServer server = ProxyManager.getProxyServer(cd.getProxyName());
            // 移除应答者
            Replier replier = server.removeReplier(appSocket);
            if (replier != null) {
                // 获取应答者连接建立锁
                Object connectLock = replier.getConnectLock();
                // 通知连接建立成功
                synchronized (connectLock) {
                    replier.setConnected(false);
                    connectLock.notify();
                }
            }
            log.info("应用客户端:[{}]与目标服务器:[{}]连接建立失败", cd.getAppSocketClient(), cd.getTargetAddress());
        }
    },
    Disconnect(4) {
        @Override
        public void execute(ChannelData cd) throws Exception {
            // 获取应用客户端地址
            String appSocket = cd.getAppSocketClient();
            
            // 获取代理
            Proxy proxy = null;
            String sc = "";
            switch (MessageType.get(cd.getMessageTypeCode())) {
                case ServerToClient:
                    proxy = ProxyManager.getProxyClient(cd.getProxyName());
                    sc = "客户端";
                    break;
                case ClientToServer:
                    proxy = ProxyManager.getProxyServer(cd.getProxyName());
                    sc = "服务端";
                    break;
                default:
                    throw new RuntimeException("消息类型错误，当前消息类型代码:" + cd.getMessageTypeCode());
            }
            // 移除应答者
            Replier replier = proxy.removeReplier(appSocket);
            // 关闭通道
            if (replier != null) {
                // 日志记录
                log.info("连接关闭，客户端:[{}]，代理({}):[{}]，服务端:[{}]",
                        appSocket, sc, replier.getChannelInfo().getLocalAddress(),
                        replier.getChannelInfo().getRemoteAddress());
                replier.setConnected(false);
                replier.close();
            }
        }
    },
    SendData(5) {
        @Override
        public void execute(ChannelData cd) {
            // 获取应用客户端地址
            String appSocket = cd.getAppSocketClient();
            // 获取消息类型
            int messageType = cd.getMessageTypeCode();
            // 获取代理
            Proxy proxy = null;
            if (MessageType.ClientToServer.getCode() == messageType) {
                proxy = ProxyManager.getProxyServer(cd.getProxyName());
            } else if (MessageType.ServerToClient.getCode() == messageType) {
                proxy = ProxyManager.getProxyClient(cd.getProxyName());
            } else {
                throw new RuntimeException("消息类型错误，当前消息类型代码:" + messageType);
            }
            // 获取应答者
            Replier replier = proxy.getReplier(appSocket);
            if (replier != null) {
                // 获取发送数据
                replier.send(cd.getSeq(), cd.getData());
            }
        }
    },
    Heartbeat(6) {
        @Override
        public void execute(ChannelData cd) throws Exception {
            log.info("收到心跳:{}", cd.toString());
        }
    };
    private static final Map<Integer, Command> COMMANDS = new HashMap<>();
    
    public static Command get(int cmd) {
        return COMMANDS.get(cmd);
    }
    
    private final int code;
    
    private Command(int code) {
        this.code = code;
    }
    
    public int getCode() {
        return code;
    }
    
    public abstract void execute(ChannelData cd) throws Exception;
    
    static {
        for (Command command : values()) {
            COMMANDS.put(command.getCode(), command);
        }
    }
}
