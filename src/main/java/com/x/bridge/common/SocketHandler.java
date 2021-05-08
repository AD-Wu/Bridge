package com.x.bridge.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @Desc
 * @Date 2020/10/21 20:16
 * @Author AD
 */
final class SocketHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private final ISocketListener listener;

    SocketHandler(ISocketListener listener) {
        this.listener = listener;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        listener.active(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        listener.inActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        listener.receive(ctx, msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (IdleState.ALL_IDLE == event.state()) {
                listener.timeout(ctx, event);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        listener.error(ctx, cause);
    }

}
