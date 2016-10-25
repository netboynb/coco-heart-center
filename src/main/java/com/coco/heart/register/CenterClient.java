package com.coco.heart.register;

import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coco.heart.common.RegisterHolder;
import com.coco.heart.common.Utils;
import com.coco.heart.entry.PingEntry;
import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.ms.coco.registry.common.CocoUtils;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年4月18日 下午2:42:40
 * @func
 */
public class CenterClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(CenterClient.class);
    private String spacename;
    private String registerUrl;
    private Gson gson = new Gson();

    public CenterClient(String spacename, String registerUrl) {
        super();
        this.spacename = spacename;
        this.registerUrl = registerUrl;
    }

    public CenterClient() {
        super();
    }

    public boolean register(String item) {
        CuratorFramework client = RegisterHolder.getClient(registerUrl);
        String path = Utils.buildPath("", spacename, item);
        boolean result = false;
        try {
            CocoUtils.createNode(client, path);
            result = CocoUtils.checkNode(client, path);
        } catch (Exception e) {
            result = false;
            LOGGER.error("heart-center-client register to zk error,info={}", e.toString());
        }
        return result;
    }

    public boolean removeMyself(String item) {
        CuratorFramework client = RegisterHolder.getClient(registerUrl);
        String path = Utils.buildPath("", spacename, item);
        boolean result = false;
        try {
            CocoUtils.removePath(client, path);
        } catch (Exception e) {
            result = false;
            LOGGER.error("heart-center-client remove to zk error,info={}", e.toString());
        }
        return result;
    }

    /**
     * 
     * TODO: 获取兄弟节点
     *
     * @return
     */
    public List<String> fetchBrothers() {
        List<String> result = null;
        CuratorFramework client = RegisterHolder.getClient(registerUrl);
        String path = Utils.buildPath("", spacename);
        try {
            boolean isexist = CocoUtils.checkNode(client, path);
            if (!isexist) {
                return null;
            }
            result = client.getChildren().forPath(path);
        } catch (Exception e) {
            LOGGER.error("fetch brothers error,info={}", e.toString());
        }
        return result;
    }

    /**
     * 
     * TODO: fetch brother's blackList
     *
     * @return
     */
    public List<PingEntry> fetchBlackListFromBrother() {
        List<PingEntry> result = null;
        List<String> brothersIpPort = fetchBrothers();
        if (brothersIpPort == null || brothersIpPort.size() < 1) {
            return result;
        }
        String[] array = brothersIpPort.get(0).split(":");
        String ipStr = array[0];
        int port = Integer.valueOf(array[1]);
        String pingEntryListStr = buildClient(ipStr, port);
        LOGGER.info("init native blackList from [{}]", ipStr);
        if (pingEntryListStr == null) {
            return result;
        }
        result = gson.fromJson(pingEntryListStr, new com.google.gson.reflect.TypeToken<List<PingEntry>>() {}.getType());
        return result;
    }

    /**
     * 
     * TODO: build http client to fetch info
     *
     * @param ip
     * @param port
     * @return
     */
    private String buildClient(String ip, int port) {
        String result = null;
        CloseableHttpClient client = HttpClients.createDefault();
        URIBuilder builder = new URIBuilder();
        String urlPath = Utils.blackListUrlPath.get();
        builder.setScheme("http").setHost(ip).setPort(port).setPath(urlPath);

        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(3000).setConnectionRequestTimeout(1000)
                .setSocketTimeout(1000).build();
        HttpGet request;
        try {
            request = new HttpGet(builder.build());
            request.setConfig(requestConfig);
            CloseableHttpResponse httpResponse = client.execute(request);
            HttpEntity entity = httpResponse.getEntity();
            result = EntityUtils.toString(entity);
        } catch (Exception e) {
            LOGGER.error("fetch brother's blackList error,info={}", e.toString());
        }

        return result;
    }

    public String getSpacename() {
        return spacename;
    }

    public void setSpacename(String spacename) {
        this.spacename = spacename;
    }

    public String getRegisterUrl() {
        return registerUrl;
    }

    public void setRegisterUrl(String registerUrl) {
        this.registerUrl = registerUrl;
    }

    public String centerInfo() {
        return Joiner.on("-").join(spacename, registerUrl);
    }
}
