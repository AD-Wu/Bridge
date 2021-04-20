package com.x.bridge.common;

import com.x.doraemon.util.StringHelper;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.TimeUnit;

/**
 * @Desc TODO
 * @Date 2020/10/21 20:53
 * @Author AD
 */
@Log4j2
public class SocketClient implements Runnable{
    
    private volatile boolean connected;
    
    private Channel channel;
    
    private final SocketConfig config;
    
    private final ISocketListener listener;
    
    public SocketClient(SocketConfig config, ISocketListener listener){
        this.config = config;
        this.listener = listener;
    }
    @Override
    public void run(){
        connect();
    }
    
    public void connect() {
        NioEventLoopGroup worker = new NioEventLoopGroup(1);
        Bootstrap boot = new Bootstrap();
        boot.group(worker);
        boot.channel(NioSocketChannel.class);
        boot.option(ChannelOption.SO_KEEPALIVE, true);
        
        boot.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) throws Exception {
                ChannelPipeline p = channel.pipeline();
                // 设置超时时间
                p.addLast(new IdleStateHandler(
                        config.getReadTimeout(),
                        config.getWriteTimeout(),
                        config.getIdleTimeout(),
                        TimeUnit.MINUTES
                ));
                // 设置socket通道监听器
                p.addLast(new SocketHandler(listener));
            }
        });
        
        try {
            ChannelFuture future = boot.connect(config.getIp(), config.getPort()).sync();
            connected = true;
            channel = future.channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error(StringHelper.getExceptionTrace(e));
        } finally {
            connected = false;
            worker.shutdownGracefully();
        }
    }
    
    public void disconnect(){
        if(connected){
            channel.close();
            connected = false;
        }
    }
}
