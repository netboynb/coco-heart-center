package com.coco.heart;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.coco.heart.common.Utils;
import com.coco.heart.servlet.BaseServlet;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年3月25日 下午3:45:37
 * @func
 */
public class JettyHeartServer extends HeartCenterServer {
    private Map<String, BaseServlet> servlets = new LinkedHashMap<String, BaseServlet>();
    @Autowired
    protected Server server;
    @Override
    public void init() throws Exception {
        port = server.getConnectors()[0].getPort();
        WebAppContext context = (WebAppContext) server.getHandler();
        for (java.util.Map.Entry<String, BaseServlet> entry : servlets.entrySet()) {
            BaseServlet servlet = entry.getValue();
            String key = entry.getKey();
            context.addServlet(new ServletHolder(servlet), key);
        }
        Utils.sysInit(dynamicConfName);
        server.start();
    }

    public void close() throws Exception {
        super.close();
        server.stop();
        server.destroy();
    }

    public Map<String, BaseServlet> getServlets() {
        return servlets;
    }
}
