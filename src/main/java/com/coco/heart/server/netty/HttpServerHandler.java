package com.coco.heart.server.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.coco.heart.domain.HeartRequest;
import com.coco.heart.domain.HeartResponse;
import com.coco.heart.handler.DispatchHandler;
import com.coco.heart.handler.PayloadHandler;
import com.coco.utils.web.Proto;
import com.coco.utils.web.Protos;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年7月8日 下午4:24:03
 * @func
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerHandler.class);
    @Autowired
    private PayloadHandler payloadHandler;
    @Autowired
    private DispatchHandler dispatchHandler;

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        Proto result = Protos.OK;
        int status = 200;
        DefaultFullHttpResponse httpResponse = null;
        if (msg.method() == HttpMethod.GET) {
            FullHttpRequest request = (FullHttpRequest) msg;
            HeartRequest heartRequest = payloadHandler.parseRequest(request);
            long id = heartRequest.getId();
            HeartResponse heartResponse = new HeartResponse().setId(id);
            dispatchHandler.dispatch(heartRequest, heartResponse);

            httpResponse = heartResponse.getResponse();
            ctx.writeAndFlush(httpResponse);
            LOGGER.debug("request:{},info={}", id, heartRequest);
            return;
        }
        // other request type
        result = Protos.InvalidRequest("just support GET");
        httpResponse = buildResponse(result, status);
        ctx.writeAndFlush(httpResponse);
    }


}
