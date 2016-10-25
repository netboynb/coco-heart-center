package com.coco.heart.filter;

import com.coco.heart.common.Utils;
import com.coco.heart.domain.HeartRequest;
import com.coco.heart.domain.HeartResponse;
import com.coco.utils.Consts;
import com.coco.utils.web.Proto;
import com.coco.utils.web.Protos;

import io.netty.handler.codec.http.DefaultFullHttpResponse;

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
            DefaultFullHttpResponse response = Utils.buildResponse(result, 200);
            heartResponse.setResponse(response);
            isGoOn = false;
        }
        return isGoOn;
    }
}
