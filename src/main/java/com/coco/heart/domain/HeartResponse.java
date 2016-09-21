package com.coco.heart.domain;

import com.coco.utils.web.Proto;

import io.netty.handler.codec.http.DefaultFullHttpResponse;

/**
* @author wanglin/netboy
* @version 创建时间：2016年9月21日 下午6:50:27
* @func 
*/
public class HeartResponse {
    private Long id;
    private String method;
    private Proto proto;
    private DefaultFullHttpResponse response;

    public HeartResponse(Long id, String method, Proto proto) {
        super();
        this.id = id;
        this.method = method;
        this.proto = proto;
    }

    public HeartResponse(Long id, String method, Proto proto, DefaultFullHttpResponse response) {
        super();
        this.id = id;
        this.method = method;
        this.proto = proto;
        this.response = response;
    }

    public HeartResponse() {}

    public Long getId() {
        return id;
    }

    public HeartResponse setId(Long id) {
        this.id = id;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public HeartResponse setMethod(String method) {
        this.method = method;
        return this;
    }

    public Proto getProto() {
        return proto;
    }

    public HeartResponse setProto(Proto proto) {
        this.proto = proto;
        return this;
    }

    public DefaultFullHttpResponse getResponse() {
        return response;
    }

    public void setResponse(DefaultFullHttpResponse response) {
        this.response = response;
    }

}
