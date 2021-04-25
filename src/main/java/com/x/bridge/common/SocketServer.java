package com.x.bridge.common;

import com.x.bridge.proxy.core.ProxyServerListener;
import com.x.doraemon.util.StringHelper;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Desc TODO
 * @Date 2020/10/21 18:53
 * @Author AD
 */
@Log4j2
public class SocketServer{
    
    private volatile boolean started = false;
    
    private Channel channel;
    
    private final SocketConfig config;
    
    private final IServerListener listener;
    
    private ExecutorService serverThread;
    
    public SocketServer(SocketConfig config, IServerListener listener) {
        this.config = config;
        this.listener = listener;
    }
    
    public static void main(String[] args) throws InterruptedException {
        SocketConfig c = new SocketConfig(1234);
        SocketServer server = new SocketServer(c, new ProxyServerListener(null));
        server.start();
        TimeUnit.SECONDS.sleep(3);
        System.out.println("启动");
        SocketClient client = new SocketClient(c, new ISocketListener() {
            @Override
            public void active(ChannelHandlerContext ctx) throws Exception {
                System.out.println("连接激活");
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
        });
        client.connect();
        System.out.println("连接");
        
        
        
    }
    
    public synchronized void start() {
        if (started) {
            listener.onServerStart(config);
        } else {
            serverThread = Executors.newSingleThreadExecutor();
            serverThread.execute(new Runnable() {
                @Override
                public void run() {
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
    
                    EventLoopGroup boss = new NioEventLoopGroup(1);
                    EventLoopGroup worker = new NioEventLoopGroup(100);
                    boot.group(boss, worker);
                    try {
                        ChannelFuture future = boot.bind(config.getPort()).sync();
                        channel = future.channel();
                        started = true;
                        listener.onServerStart(config);
                        channel.closeFuture().sync();// 阻塞当前服务
                    } catch (Exception e) {
                        started = false;
                        log.error(StringHelper.getExceptionTrace(e));
                        listener.onServerStartError(e);
                    } finally {
                        boss.shutdownGracefully();
                        worker.shutdownGracefully();
                    }
                }
            });
            
        }
    }
    
    public synchronized void stop() {
        if (started) {
            channel.close();
            started = false;
            listener.onServerStop(config);
            serverThread.shutdown();
        }
    }
    
}
