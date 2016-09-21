package com.coco.heart.core;

import java.io.IOException;
import java.util.concurrent.Callable;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coco.heart.common.HttpClientHolder;
import com.coco.heart.common.Utils;
import com.coco.heart.entry.PingEntry;
import com.coco.heart.redis.pubsub.PubRedisService;
import com.google.common.base.Joiner;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年3月25日 下午5:26:57
 * @func
 */
public class BizTask implements Callable<Object> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BizTask.class);
    private PingEntry pingEntry;
    private PubRedisService pubRedisService;
    private long traceId;

    public BizTask(PingEntry pingEntry, long traceId, PubRedisService pubRedisService) {
        this.pingEntry = pingEntry;
        this.traceId = traceId;
        this.pubRedisService = pubRedisService;
    }

    @Override
    public Object call() throws Exception {
        boolean enablePing = Utils.enabeSelfPing.get();
        boolean canDoBiz = true;
        boolean result = false;
        // enable self ping to check the host
        if (enablePing) {
            // ping to check the host
            LOGGER.info("[ traceId-{} ] volunte  ping the black host", traceId);
            if (ping(pingEntry)) {
                canDoBiz = false;
            }
        }
        if (canDoBiz) {
            // add to black set
            pubRedisService.pub(Joiner.on(Utils.pubSubKey).join(Utils.hostDead, pingEntry.toKeyWithTraceId()));
            // update the node's status on the zookeeper
            result = Utils.updateHostStatus(pingEntry.getServiceName(), pingEntry.getGroupName(),
                    pingEntry.getHostKey(), false, pingEntry.getZkurl());
            LOGGER.error("[ traceId-{} ]  opt register [{}],info={}", traceId, result, pingEntry.toString());
        }
        return result;
    }

    /**
     * 
     * TODO: 能ping通 目标实例 则返回true
     *
     * @param pingEntry
     * @return
     */
    private boolean ping(PingEntry pingEntry) {
        CloseableHttpClient client = HttpClientHolder.getHttpClient();
        URIBuilder builder = new URIBuilder();
        String[] array = pingEntry.getHostKey().split(":");
        int port = Integer.valueOf(array[1]) + 1;
        builder.setScheme("http").setHost(array[0]).setPort(port).setPath(Utils.pingUri.get());
        HttpGet request = null;
        CloseableHttpResponse httpResponse = null;
        boolean result = false;
        try {
            request = new HttpGet(builder.build());
            RequestConfig requestConfig =
                    RequestConfig.custom().setSocketTimeout(Utils.httpClientTimeoutInMillisecond.get()).build();// 设置请求和传输超时时间
            request.setConfig(requestConfig);
            httpResponse = client.execute(request);
            result = httpResponse.getStatusLine().getStatusCode() == 200 ? true : false;
        } catch (Exception e) {
            LOGGER.error("[ traceId-{} ]  主动 ping ,close httpResponse error info={}", traceId, e.toString());
        } finally {
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    LOGGER.error("[ traceId-{} ]  主动 ping ,close httpResponse error info={}", traceId, e.toString());
                }
            }
        }
        LOGGER.info("[ traceId-{} ]  主动 ping result ={}", traceId, result);
        return result;
    }

}
