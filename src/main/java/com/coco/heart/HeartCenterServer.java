package com.coco.heart;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.coco.heart.entry.PingEntry;
import com.coco.heart.handler.PayloadHandler;
import com.coco.heart.register.CenterClient;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年7月6日 下午4:43:44
 * @func
 */
public abstract class HeartCenterServer {
    protected static final Logger LOGGER = LoggerFactory.getLogger(HeartCenterServer.class);
    @Autowired
    protected Integer port;
    @Autowired
    protected String dynamicConfName = "heartCenterDynamic.properties";
    @Autowired
    protected PayloadHandler payloadHandler;
    @Autowired
    protected CenterClient centerClient;

    abstract void init() throws Exception;

    /**
     * 
     * 将该节点注册到zookeeper上
     *
     * @throws Exception
     */
    protected void register() throws Exception {
        String ipPort = myselfIp() + ":" + port;
        // fetch the blackList from brother node, to init native
        fetchNodeBlacklist();
        // register to zookeeper
        centerClient.register(ipPort);
        LOGGER.info("[register] {} to zookeeper={}", ipPort, centerClient.centerInfo());
    };

    public void start() throws Exception {
        init();
        if (payloadHandler != null) {
            payloadHandler.init();
        }
        register();
        LOGGER.info("heart-center-server started,listen port={},centerInfo={}", port, centerClient.centerInfo());
    }

    public void close() throws Exception {
        // remove node from zookeeper
        String ipPort = myselfIp() + ":" + port;
        centerClient.removeMyself(ipPort);
        LOGGER.warn("heart-center-server closed ,remove itself from centerInfo={}", centerClient.centerInfo());
        if (payloadHandler != null) {
            payloadHandler.close();
        }
    }

    private void fetchNodeBlacklist() {
        LOGGER.info("start init native blackList from brother node ");
        List<PingEntry> list = centerClient.fetchBlackListFromBrother();
        if (list == null || list.size() < 1) {
            return;
        }
        payloadHandler.addBlackList(list);
        LOGGER.info("end init native blackList from brother node ");
    }

    private String myselfIp() throws UnknownHostException {
        InetAddress addr = InetAddress.getLocalHost();
        String ip = addr.getHostAddress();
        return ip;
    }
}
