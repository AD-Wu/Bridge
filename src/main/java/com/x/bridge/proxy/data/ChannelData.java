package com.x.bridge.proxy.data;

import com.pikachu.common.annotations.IColumn;
import com.pikachu.common.annotations.ITable;
import com.x.bridge.proxy.core.Command;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.StringJoiner;

/**
 * @Desc
 * @Date 2020/10/22 22:02
 * @Author AD
 */
@Data
@Builder
@NoArgsConstructor
@ITable(doc = "通道数据基类", cache = false)
public class ChannelData {
    
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
    
    // ------------------------ 变量定义 ------------------------
    // ------------------------ 构造方法 ------------------------
    // ------------------------ 方法定义 ------------------------
    // ------------------------ 私有方法 ------------------------
    private void autoCreateMethod() {}
    
}
