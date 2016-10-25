package com.coco.heart;

import org.springframework.beans.factory.annotation.Autowired;

import com.coco.heart.common.Utils;
import com.coco.heart.server.netty.HttpServerInitializer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年7月8日 下午4:54:03
 * @func
 */
public final class NettyHeartServer extends HeartCenterServer {
    @Autowired
    private Integer masterThreadNum = 4;
    @Autowired
    private Integer workerThreadNum = Runtime.getRuntime().availableProcessors() * 2 + 2;
    // @Autowired
    // private ServletContextListener listener;
    @Autowired
    private HttpServerInitializer httpServerInitializer;

    @Override
    public void init() throws Exception {
        LOGGER.info("start init http-netty server");
        try {
            if (httpServerInitializer == null) {
                httpServerInitializer = new HttpServerInitializer();
            }
            httpServerInitializer.init();
            LOGGER.info("end init http-netty server");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Utils.sysInit(dynamicConfName);
        initNettty();
    }

    protected void initNettty() throws Exception {
        // Configure the server.
        EventLoopGroup bossGroup = new NioEventLoopGroup(masterThreadNum); // 1 port - 1 thread
        EventLoopGroup workerGroup = new NioEventLoopGroup(workerThreadNum);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(httpServerInitializer);

            // Bind and start to accept incoming connections.
            Channel ch = b.bind(port).sync().channel();
            LOGGER.info("start http-netty server,port ={},masterThreadNum={},workerThreadNum={}", port, masterThreadNum,
                    workerThreadNum);
            ch.closeFuture().sync();
        } finally {
            // listener.contextDestroyed(null);
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void start() throws Exception {
        super.start();
        // TODO something
    }

    @Override
    public void close() throws Exception {
        super.close();
        // TODO something
    }

    public NettyHeartServer() {}
}
