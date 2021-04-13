package com.x.bridge.common;

import com.x.doraemon.util.Strings;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.TimeUnit;

/**
 * @Desc TODO
 * @Date 2020/10/21 18:53
 * @Author AD
 */
@Log4j2
public class SocketServer implements Runnable {
    
    private volatile boolean started = false;
    
    private Channel channel;
    
    private final SocketConfig config;
    
    private final IServerListener listener;
    
    public SocketServer(SocketConfig config, IServerListener listener) {
        this.config = config;
        this.listener = listener;
    }
    
    @Override
    public void run() {
        start();
    }
    
    public void start() {
        if (started) {
            listener.onServerStart(config);
        } else {
            ServerBootstrap boot = new ServerBootstrap();
            boot.channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_BACKLOG, config.getBacklog())
                    .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(config.getRecvBuf()));
            
            boot.childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel channel) throws Exception {
                    ChannelPipeline p = channel.pipeline();
                    // 设置超时事件处理器
                    p.addLast(new IdleStateHandler(
                            config.getReadTimeout(),
                            config.getWriteTimeout(),
                            config.getIdleTimeout(),
                            TimeUnit.MINUTES
                    ));
                    // 设置通道监听器
                    p.addLast(new SocketHandler(listener));
                }
            });
           
            EventLoopGroup boss = new NioEventLoopGroup();
            EventLoopGroup worker = new NioEventLoopGroup();
            boot.group(boss, worker);
            try {
                ChannelFuture future = boot.bind(config.getPort()).sync();
                channel = future.channel();
                started = true;
                listener.onServerStart(config);
                channel.closeFuture().sync();// 阻塞当前服务
            } catch (Exception e) {
                started = false;
                log.error(Strings.getExceptionTrace(e));
                listener.onServerStartError(e);
            } finally {
                boss.shutdownGracefully();
                worker.shutdownGracefully();
            }
        }
    }
    
    public void stop() {
        if (started) {
            channel.close();
            started = false;
            listener.onServerStop(config);
        }
    }
    
}
