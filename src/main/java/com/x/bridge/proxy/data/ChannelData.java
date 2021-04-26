package com.x.bridge.proxy.data;

import com.pikachu.common.annotations.IColumn;
import com.pikachu.common.annotations.ITable;
import com.x.bridge.proxy.core.Command;

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
    
    @IColumn(doc = "数据帧序号",pk = true)
    protected long seq;
    
    protected String proxyName;
    
    protected String proxyAddress;

    protected String targetAddress;
    
    /**
     * 对应枚举类
     * {@link Command}
     */
    protected Command command;
    /**
     * 对应枚举类
     * {@link MessageType}
     */
    protected MessageType messageType;

    protected byte[] data;
    
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
    
    public String getProxyAddress() {
        return proxyAddress;
    }
    
    public void setProxyAddress(String proxyAddress) {
        this.proxyAddress = proxyAddress;
    }
    
    public String getTargetAddress() {
        return targetAddress;
    }
    
    public void setTargetAddress(String targetAddress) {
        this.targetAddress = targetAddress;
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
                .add("proxyAddress='" + proxyAddress + "'")
                .add("targetAddress='" + targetAddress + "'")
                .add("command=" + command)
                .add("messageType=" + messageType)
                .add("data=" + Arrays.toString(data))
                .toString();
    }
    
}
