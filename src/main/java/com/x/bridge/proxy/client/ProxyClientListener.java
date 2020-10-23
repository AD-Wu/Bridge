package com.x.bridge.proxy.client;

import com.x.bridge.core.ISocketListener;
import com.x.bridge.data.ChannelInfo;
import com.x.bridge.data.ReplierManager;
import com.x.bridge.util.SocketHelper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.log4j.Log4j2;

/**
 * @Desc TODO
 * @Date 2020/10/23 09:31
 * @Author AD
 */
@Log4j2
public class ProxyClientListener implements ISocketListener {

    private final String proxyKey;
    private final ReplierManager replierManager;

    public ProxyClientListener(String proxyKey, ReplierManager replierManager) {
        this.proxyKey = proxyKey;
        this.replierManager = replierManager;
    }

    @Override
    public void active(ChannelHandlerContext ctx) throws Exception {
        //Replier replier = new Replier(ctx);
        //replier.recvSeqIncrement();

        ChannelInfo channelInfo = SocketHelper.getChannelInfo(ctx);
        System.out.println("通道激活");
        System.out.println(channelInfo);

    }

    @Override
    public void inActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void receive(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {

    }

    @Override
    public void timeout(ChannelHandlerContext ctx, IdleStateEvent event) throws Exception {

    }

    @Override
    public void error(ChannelHandlerContext ctx, Throwable cause) throws Exception {

    }

}
