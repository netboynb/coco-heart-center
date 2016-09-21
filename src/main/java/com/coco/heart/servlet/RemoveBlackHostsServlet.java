package com.coco.heart.servlet;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年3月24日 下午6:39:24
 * @func
 */
public class RemoveBlackHostsServlet extends BaseServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoveBlackHostsServlet.class);
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String removeBlacklistStr = req.getParameter("removeBlackList");
        List<String> result = Collections.emptyList();
        if (!StringUtils.isEmpty(removeBlacklistStr)) {
            String[] array = removeBlacklistStr.split(",");
            result = payloadHandler.removeBlackList(Lists.newArrayList(array));
        }
        printout(result, resp);
    }
}
