package com.coco.heart.server.netty;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年7月6日 下午4:43:44
 * @func
 */
public interface HeartCenterServer {
    void init() throws Exception;

    void start() throws Exception;

    void close() throws Exception;
}
