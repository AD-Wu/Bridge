package com.x.bridge.common;

import com.x.doraemon.util.StringHelper;
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
public class SocketServer {

    private volatile boolean started = false;

    private Channel channel;

    private EventLoopGroup boss;

    private EventLoopGroup worker;

    private final SocketConfig config;

    private final IServerListener listener;

    public SocketServer(SocketConfig config, IServerListener listener) {
        this.config = config;
        this.listener = listener;
    }

    public synchronized void start() {
        if (started) {
            return;
        }
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
        // 创建线程池
        boss = new NioEventLoopGroup(1);
        worker = new NioEventLoopGroup(100);
        boot.group(boss, worker);
        try {
            ChannelFuture future = boot.bind(config.getPort()).sync();
            channel = future.channel();
            started = true;
            listener.onServerStart(config);
        } catch (Exception e) {
            started = false;
            log.error(StringHelper.getExceptionTrace(e));
            listener.onServerStartError(e);
            stop();
        }
    }

    public synchronized void stop() {
        if (channel != null) {
            channel.close();
        }
        if (worker != null) {
            worker.shutdownGracefully();
        }
        if (boss != null) {
            boss.shutdownGracefully();
        }
        listener.onServerStop(config);
        started = false;
    }

}
