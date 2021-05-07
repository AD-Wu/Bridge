package com.x.bridge.data;

import com.pikachu.common.annotations.IColumn;
import com.pikachu.common.annotations.ITable;
import com.x.bridge.proxy.core.Command;
import com.x.bridge.proxy.core.Replier;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * @Desc
 * @Date 2020/10/22 22:02
 * @Author AD
 */
@ITable(doc = "通道数据基类", cache = false)
public class ChannelData implements Serializable {
    
    @IColumn(doc = "客户端socket地址", pk = true)
    protected String appClient;
    @IColumn(doc = "数据帧序号", pk = true)
    protected long seq;
    protected String proxyServer;
    
    protected String proxyClient;
    protected String appServer;
    
    protected Command command;
    protected byte[] data;
    protected String exception;
    
    public static ChannelData generate(Replier replier) {
        ChannelData cd = new ChannelData();
        cd.setAppClient(replier.getAppClient());
        cd.setSeq(replier.getRecvSeq());
        cd.setProxyServer(replier.getProxyServer());
        cd.setProxyClient(replier.getProxyClient());
        cd.setAppServer(replier.getAppServer());
        return cd;
    }
    
    public String getAppClient() {
        return appClient;
    }
    
    public void setAppClient(String appClient) {
        this.appClient = appClient;
    }
    
    public long getSeq() {
        return seq;
    }
    
    public void setSeq(long seq) {
        this.seq = seq;
    }
    
    public String getProxyServer() {
        return proxyServer;
    }
    
    public void setProxyServer(String proxyServer) {
        this.proxyServer = proxyServer;
    }
    
    public String getProxyClient() {
        return proxyClient;
    }
    
    public void setProxyClient(String proxyClient) {
        this.proxyClient = proxyClient;
    }
    
    public String getAppServer() {
        return appServer;
    }
    
    public void setAppServer(String appServer) {
        this.appServer = appServer;
    }
    
    public Command getCommand() {
        return command;
    }
    
    public void setCommand(Command command) {
        this.command = command;
    }
    
    public byte[] getData() {
        return data;
    }
    
    public void setData(byte[] data) {
        this.data = data;
    }
    
    public String getException() {
        return exception;
    }
    
    public void setException(String exception) {
        this.exception = exception;
    }
    
    @Override
    public String toString() {
        return new StringJoiner(", ", ChannelData.class.getSimpleName() + "[", "]")
                .add("appSocketClient='" + appClient + "'")
                .add("seq=" + seq)
                .add("proxyAddress='" + proxyServer + "'")
                .add("targetAddress='" + appServer + "'")
                .add("command=" + command)
                .add("dataLength=" + data.length)
                .add("exception=" + exception)
                .toString();
    }
    
}
