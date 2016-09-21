package com.coco.heart.common;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年4月1日 下午3:27:04
 * @func
 */
public class HttpClientHolder {
    private static volatile HttpClientHolder httpClientHolder;
    private CloseableHttpClient client;

    private HttpClientHolder() {
        client = HttpClients.createDefault();
    }

    public static CloseableHttpClient getHttpClient() {
        if (httpClientHolder == null) {
            synchronized (HttpClientHolder.class) {
                if (httpClientHolder == null) {
                    httpClientHolder = new HttpClientHolder();
                }

            }
        }
        return httpClientHolder.client;

    }

}
