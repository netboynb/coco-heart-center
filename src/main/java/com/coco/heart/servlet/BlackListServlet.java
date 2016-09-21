package com.coco.heart.servlet;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.coco.heart.entry.PingEntry;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年3月24日 下午6:38:41
 * @func
 */
public class BlackListServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        List<PingEntry> blackList = payloadHandler.getBlackList();
        printout(blackList, resp);
    }
}
