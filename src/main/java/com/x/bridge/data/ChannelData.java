package com.x.bridge.data;

import com.x.bridge.command.core.Command;
import lombok.Data;

/**
 * @Desc TODO
 * @Date 2020/10/22 22:02
 * @Author AD
 */
@Data
public class ChannelData {
    private String remoteAddress;
    private int proxyPort;
    private String targetIp;
    private int targetPort;
    private long recvSeq;
    private Command cmd;
    private byte[] data;
}
