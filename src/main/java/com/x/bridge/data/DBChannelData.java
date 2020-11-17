package com.x.bridge.data;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Data
@Entity
public class DBChannelData implements Serializable {

    @Id
    @GeneratedValue
    private long id;

    private String proxyName;

    private String appSocketClient;

    private String proxyAddress;

    private String targetAddress;

    private long recvSeq;

    private int command;

    private int messageType;

    private byte[] data;
}
