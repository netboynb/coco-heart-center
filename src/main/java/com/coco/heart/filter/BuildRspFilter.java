package com.coco.heart.filter;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.nio.charset.StandardCharsets;

import com.coco.heart.common.Utils;
import com.coco.heart.domain.HeartRequest;
import com.coco.heart.domain.HeartResponse;
import com.coco.utils.Consts;
import com.coco.utils.web.Proto;
import com.coco.utils.web.Protos;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
* @author wanglin/netboy
* @version 创建时间：2016年9月21日 下午7:07:38
* @func 
*/
public class BuildRspFilter implements HeartFilter {

    @Override
    public boolean filter(HeartRequest heartRequest, HeartResponse heartResponse) {
        boolean isGoOn = true;
        String method = heartRequest.getMethod();
        if (method.equals(Consts.heartPath)) {
            Proto result = Protos.OK;
            DefaultFullHttpResponse response = buildResponse(result, 200);
            heartResponse.setResponse(response);
            isGoOn = false;
        }
        return isGoOn;
    }

    private DefaultFullHttpResponse buildResponse(Proto result, int status) {
        byte[] resultStr = Utils.gson.toJson(result).getBytes(StandardCharsets.UTF_8);
        int length = resultStr.length;
        ByteBuf byteBuf = Unpooled.wrappedBuffer(resultStr);
        DefaultFullHttpResponse response =
                new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.valueOf(status), byteBuf);
        response.headers().add("Access-Control-Allow-Origin", "*"); // 允许跨域
        response.headers().add("pragma", "no-cache");
        response.headers().add("cache-control", "no-cache");
        response.headers().add("Content-Type", "application/json; charset=UTF-8");
        response.headers().add("Content-Length", length);
        return response;
    }
}
