package com.coco.heart;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coco.heart.common.Utils;
import com.coco.heart.core.HeartCore;
import com.coco.heart.entry.PingEntry;
import com.coco.heart.handler.PayloadHandler;
import com.coco.heart.register.CenterClient;
import com.coco.heart.server.netty.HeartCenterServer;
import com.coco.heart.servlet.BaseServlet;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年3月25日 下午3:45:37
 * @func
 */
public class JettyHeartServer implements HeartCenterServer {
    private static final Logger logger = LoggerFactory.getLogger(JettyHeartServer.class);
    private Integer port;
    private Server server;
    private Map<String, BaseServlet> servlets = new LinkedHashMap<String, BaseServlet>();
    private PayloadHandler payloadHandler;
    private CenterClient centerClient;
    private String dynamicConfName = "heartCenterDynamic.properties";

    @Override
    public void init() throws Exception {
        port = server.getConnectors()[0].getPort();
        WebAppContext context = (WebAppContext) server.getHandler();
        for (java.util.Map.Entry<String, BaseServlet> entry : servlets.entrySet()) {
            BaseServlet servlet = entry.getValue();
            String key = entry.getKey();
            servlet.setHeartCore(heartCore);
            context.addServlet(new ServletHolder(servlet), key);
        }
        Utils.sysInit(dynamicConfName);
    }

    @Override
    public void start() throws Exception {
        init();
        server.start();
        if (heartCore != null) {
            heartCore.start();
        }
        String ipPort = myselfIp() + ":" + port;
        // fetch the blackList from brother node, to init native
        fetchNodeBlacklist();
        // register to zookeeper
        centerClient.register(ipPort);
        logger.info("jetty server started, port={}", port);
    }

    private void fetchNodeBlacklist() {
        logger.info("start init native blackList from brother node ");
        List<PingEntry> list = centerClient.fetchBlackListFromBrother();
        if (list == null || list.size() < 1) {
            return;
        }
        heartCore.addBlackList(list);
        logger.info("end init native blackList from brother node ");
    }

    private String myselfIp() throws UnknownHostException {
        InetAddress addr = InetAddress.getLocalHost();
        String ip = addr.getHostAddress();
        return ip;
    }

    @Override
    public void close() throws Exception {
        // remove node from zookeeper
        String ipPort = myselfIp() + ":" + port;
        centerClient.removeMyself(ipPort);
        if (heartCore != null) {
            heartCore.close();
        }

        server.stop();
        server.destroy();
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public Map<String, BaseServlet> getServlets() {
        return servlets;
    }

    public void setServlets(Map<String, BaseServlet> servlets) {
        this.servlets = servlets;
    }

    public HeartCore getHeartCore() {
        return heartCore;
    }

    public void setHeartCore(HeartCore heartCore) {
        this.heartCore = heartCore;
    }

    public CenterClient getCenterClient() {
        return centerClient;
    }

    public void setCenterClient(CenterClient centerClient) {
        this.centerClient = centerClient;
    }

    public String getDynamicConfName() {
        return dynamicConfName;
    }

    public void setDynamicConfName(String dynamicConfName) {
        this.dynamicConfName = dynamicConfName;
    }

    public static void main(String[] args) throws Exception {
        int argSize = args.length;

        final JettyHeartServer jettyServer2 = new JettyHeartServer();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    jettyServer2.close();
                } catch (Exception e) {
                    logger.error("run main stop error!", e);
                }
            }

        });
        jettyServer2.start();
    }
}
