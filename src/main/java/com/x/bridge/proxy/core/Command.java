package com.x.bridge.proxy.core;

import com.x.bridge.common.SocketClient;
import com.x.bridge.common.SocketConfig;
import com.x.bridge.proxy.client.ProxyClient;
import com.x.bridge.proxy.client.ProxyClientListener;
import com.x.bridge.data.ChannelData;
import com.x.bridge.util.ProxyHelper;
import lombok.extern.log4j.Log4j2;

/**
 * @Desc
 * @Date 2020/10/22 21:00
 * @Author AD
 */
@Log4j2
public enum Command {
    ConnectRequest {
        @Override
        public void execute(Proxy proxy, ChannelData cd) throws Exception {
            // 获取应用客户端地址
            String appSocket = cd.getAppSocketClient();
            // 获取应答者
            Replier replier = proxy.getReplier(appSocket);
            // 未建立建立
            if (replier == null) {
                // 创建socket客户端连接目标服务器
                String ip = ProxyHelper.getIP(cd.getAppSocketServer());
                int port = ProxyHelper.getPort(cd.getAppSocketServer());
                SocketClient socket = new SocketClient(
                        new SocketConfig(ip, port),
                        new ProxyClientListener(appSocket, cd.getProxyServer(),(ProxyClient) proxy));
                socket.connect();
            } else {
                log.info("代理服务器:[{}],连接:[{}]已存在，不再重新建立",
                        cd.getProxyServer(), cd.getAppSocketClient());
            }
        }
    },
    ConnectSuccess {
        @Override
        public void execute(Proxy proxy, ChannelData cd) throws Exception {
            // 获取应用客户端地址
            String appSocket = cd.getAppSocketClient();
            // 获取应答者
            Replier replier = proxy.getReplier(appSocket);
            if (replier != null) {
                // 获取应答者连接建立锁
                Object connectLock = replier.getConnectLock();
                // 通知连接建立成功
                synchronized (connectLock) {
                    replier.setConnected(true);
                    replier.setConnectTimeout(false);
                    replier.setProxyClient(cd.getProxyClient());
                    connectLock.notify();
                }
            }
        }
    },
    ConnectFailed {
        @Override
        public void execute(Proxy proxy, ChannelData cd) throws Exception {
            // 获取应用客户端地址
            String appSocket = cd.getAppSocketClient();
            // 移除应答者
            Replier replier = proxy.removeReplier(appSocket);
            if (replier != null) {
                // 获取应答者连接建立锁
                Object connectLock = replier.getConnectLock();
                // 通知连接建立成功
                synchronized (connectLock) {
                    replier.setConnected(false);
                    connectLock.notify();
                }
            }
            log.info("应用客户端:[{}]与目标服务器:[{}]连接建立失败", cd.getAppSocketClient(), cd.getAppSocketServer());
        }
    },
    Disconnect {
        @Override
        public void execute(Proxy proxy, ChannelData cd) throws Exception {
            // 获取应用客户端地址
            String appSocket = cd.getAppSocketClient();
            String sc = cd.getMessageType() == MessageType.ServerToClient ? "客户端" : "服务端";
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
    SendData {
        @Override
        public void execute(Proxy proxy, ChannelData cd) {
            // 获取应用客户端地址
            String appSocket = cd.getAppSocketClient();
            // 获取应答者
            Replier replier = proxy.getReplier(appSocket);
            if (replier != null) {
                // 获取发送数据
                replier.send(cd.getSeq(), cd.getData());
            }
        }
    },
    Heartbeat {
        @Override
        public void execute(Proxy proxy, ChannelData cd) throws Exception {
            log.info("收到心跳:{}", cd.toString());
        }
    };
    
    public abstract void execute(Proxy proxy, ChannelData cd) throws Exception;
    
}
