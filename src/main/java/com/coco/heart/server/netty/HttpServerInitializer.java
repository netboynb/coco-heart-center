package com.coco.heart.server.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年7月8日 下午4:34:03
 * @func
 */
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

    public HttpServerInitializer() {}

    public void init() {}

    @Override
    protected void initChannel(SocketChannel sc) throws Exception {
        ChannelPipeline cp = sc.pipeline();
        cp.addLast("decoder", new HttpRequestDecoder());
        cp.addLast("aggregator", new HttpObjectAggregator(65536));
        cp.addLast("encoder", new HttpResponseEncoder());
        cp.addLast("chunkedWriter", new ChunkedWriteHandler());
        cp.addLast("handler", new HttpServerHandler());
    }
}
