package com.x.bridge.proxy.data;

import com.pikachu.common.annotations.IColumn;
import com.pikachu.common.annotations.ITable;
import com.x.bridge.proxy.command.core.Command;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    protected String proxyName;

    @IColumn(doc = "客户端socket地址", pk = true)
    protected String appSocketClient;

    protected String proxyAddress;

    protected String targetAddress;

    @IColumn(doc = "数据帧序号")
    protected long recvSeq;

    protected Command command;

    protected MessageType messageType;

    protected byte[] data;

}
