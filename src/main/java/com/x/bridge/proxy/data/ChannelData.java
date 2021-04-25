package com.x.bridge.proxy.data;

import com.pikachu.common.annotations.IColumn;
import com.pikachu.common.annotations.ITable;
import com.x.bridge.proxy.core.Command;

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
    protected int commandCode;
    /**
     * 对应枚举类
     * {@link MessageType}
     */
    protected int messageTypeCode;

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
    
    public int getCommandCode() {
        return commandCode;
    }
    
    public void setCommandCode(int commandCode) {
        this.commandCode = commandCode;
    }
    
    public int getMessageTypeCode() {
        return messageTypeCode;
    }
    
    public void setMessageTypeCode(int messageTypeCode) {
        this.messageTypeCode = messageTypeCode;
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
                .add("proxyName='" + proxyName + "'")
                .add("appSocketClient='" + appSocketClient + "'")
                .add("proxyAddress='" + proxyAddress + "'")
                .add("targetAddress='" + targetAddress + "'")
                .add("seq=" + seq)
                .add("command=" + Command.get(commandCode))
                .add("messageType=" + MessageType.get(messageTypeCode))
                .add("dataLength=" + data.length)
                .toString();
    }
    
    
}
