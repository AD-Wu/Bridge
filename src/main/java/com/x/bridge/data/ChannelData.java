package com.x.bridge.data;

import com.pikachu.common.annotations.IColumn;
import com.pikachu.common.annotations.ITable;
import com.x.bridge.proxy.core.Command;
import com.x.bridge.proxy.core.Replier;
import com.x.bridge.proxy.core.MessageType;

import java.io.Serializable;
import java.util.Arrays;
import java.util.StringJoiner;

/**
 * @Desc
 * @Date 2020/10/22 22:02
 * @Author AD
 */
@ITable(doc = "通道数据基类", cache = false)
public class ChannelData implements Serializable {

    @IColumn(doc = "客户端socket地址", pk = true)
    protected String appSocketClient;

    @IColumn(doc = "数据帧序号", pk = true)
    protected long seq;

    protected String proxyName;

    protected String proxyServer;

    protected String proxyClient;

    protected String appSocketServer;

    protected Command command;

    protected MessageType messageType;

    protected byte[] data;

    public static ChannelData generate(String proxyName, Replier replier, MessageType type) {
        ChannelData cd = new ChannelData();
        cd.setProxyName(proxyName);
        cd.setAppSocketClient(replier.getAppSocketClient());
        cd.setSeq(replier.getRecvSeq());
        cd.setProxyServer(replier.getProxyServer());
        cd.setProxyClient(replier.getProxyClient());
        cd.setAppSocketServer(replier.getAppSocketServer());
        cd.setMessageType(type);
        return cd;
    }

    public String getAppSocketClient() {
        return appSocketClient;
    }

    public void setAppSocketClient(String appSocketClient) {
        this.appSocketClient = appSocketClient;
    }

    public long getSeq() {
        return seq;
    }

    public void setSeq(long seq) {
        this.seq = seq;
    }

    public String getProxyName() {
        return proxyName;
    }

    public void setProxyName(String proxyName) {
        this.proxyName = proxyName;
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

    public String getAppSocketServer() {
        return appSocketServer;
    }

    public void setAppSocketServer(String appSocketServer) {
        this.appSocketServer = appSocketServer;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ChannelData.class.getSimpleName() + "[", "]")
                .add("appSocketClient='" + appSocketClient + "'")
                .add("seq=" + seq)
                .add("proxyName='" + proxyName + "'")
                .add("proxyAddress='" + proxyServer + "'")
                .add("targetAddress='" + appSocketServer + "'")
                .add("command=" + command)
                .add("messageType=" + messageType)
                .add("data=" + Arrays.toString(data))
                .toString();
    }

}
