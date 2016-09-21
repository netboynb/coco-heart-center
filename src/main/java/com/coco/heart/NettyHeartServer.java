package com.coco.heart;

import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coco.heart.server.netty.HeartCenterServer;
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
public final class NettyHeartServer implements HeartCenterServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyHeartServer.class);
    private Integer port = 8082;
    private Integer masterThreadNum = 4;
    private Integer workerThreadNum = Runtime.getRuntime().availableProcessors() * 2 + 2;
    private ServletContextListener listener;
    private HttpServerInitializer httpServerInitializer;

    @Override
    public void init() {
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

    }

    @Override
    public void start() throws Exception {
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
            listener.contextDestroyed(null);
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void close() throws Exception {

    }

    public NettyHeartServer() {}

    public NettyHeartServer(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Integer getMasterThreadNum() {
        return masterThreadNum;
    }

    public void setMasterThreadNum(Integer masterThreadNum) {
        this.masterThreadNum = masterThreadNum;
    }

    public Integer getWorkerThreadNum() {
        return workerThreadNum;
    }

    public void setWorkerThreadNum(Integer workerThreadNum) {
        this.workerThreadNum = workerThreadNum;
    }

}
