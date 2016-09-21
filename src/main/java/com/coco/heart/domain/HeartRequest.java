package com.coco.heart.domain;

import com.coco.utils.web.BaseDO;
import com.coco.utils.web.Proto;

/**
* @author wanglin/netboy
* @version 创建时间：2016年9月21日 下午2:41:08
* @func 
*/
public class HeartRequest extends BaseDO {
    private long id;
    private String remoteIp;
    private String uri;
    private String method;
    private Proto proto;

    public HeartRequest(Long id, String remoteIp, String uri, String method, Proto proto) {
        this.id = id;
        this.remoteIp = remoteIp;
        this.uri = uri;
        this.method = method;
        this.proto = proto;
    }

    public Proto getProto() {
        return proto;
    }

    public void setProto(Proto proto) {
        this.proto = proto;
    }

    public long getId() {
        return id;
    }

    public HeartRequest setId(long id) {
        this.id = id;
        return this;
    }

    public HeartRequest() {
        super();
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public HeartRequest setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
        return this;
    }

    public String getUri() {
        return uri;
    }

    public HeartRequest setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public HeartRequest setMethod(String method) {
        this.method = method;
        return this;
    }

}
