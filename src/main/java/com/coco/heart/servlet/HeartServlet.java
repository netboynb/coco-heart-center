package com.coco.heart.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coco.utils.Consts;
import com.coco.utils.web.Proto;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年3月24日 下午6:39:24
 * @func
 */

public class HeartServlet extends BaseServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(HeartServlet.class);
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String payload = req.getParameter(Consts.payload);
        Proto result = payloadHandler.parsePayload(payload);
        printout(result, resp);
    }
}
