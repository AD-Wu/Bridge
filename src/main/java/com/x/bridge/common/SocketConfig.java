package com.x.bridge.common;

import com.x.doraemon.util.StringHelper;
import lombok.extern.log4j.Log4j2;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Desc TODO
 * @Date 2020/10/21 19:49
 * @Author AD
 */
@Log4j2
public class SocketConfig {

    private String ip;
    private int port;
    private int bossCount = 1;
    private int workerCount = Runtime.getRuntime().availableProcessors() * 4;
    private int readTimeout = 0;
    private int writeTimeout = 0;
    private int idleTimeout = 0;
    private int backlog = 2048;
    private int recvBuf = 65536;

    public static SocketConfig getServerConfig(int port) {
        try {
           return new SocketConfig(InetAddress.getLocalHost().getHostAddress(),port);
        } catch (UnknownHostException e) {
            log.error(StringHelper.getExceptionTrace(e));
        }
        return null;
    }

    public static SocketConfig getClientConfig(String ip, int port) {
        try {
            return new SocketConfig(InetAddress.getByName(ip).getHostAddress(),port);
        } catch (UnknownHostException e) {
            log.error(StringHelper.getExceptionTrace(e));
        }
        return null;
    }

    private SocketConfig(String ip, int port){
        this.ip = ip;
        this.port = Math.max(port, 0);
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public int getBossCount() {
        return bossCount;
    }

    public void setBossCount(int bossCount) {
        this.bossCount = bossCount;
    }

    public int getWorkerCount() {
        return workerCount;
    }

    public void setWorkerCount(int workerCount) {
        this.workerCount = workerCount;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getWriteTimeout() {
        return writeTimeout;
    }

    public void setWriteTimeout(int writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public int getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(int idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public int getBacklog() {
        return backlog;
    }

    public void setBacklog(int backlog) {
        this.backlog = backlog;
    }

    public int getRecvBuf() {
        return recvBuf;
    }

    public void setRecvBuf(int recvBuf) {
        this.recvBuf = recvBuf;
    }

}
