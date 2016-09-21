package com.coco.heart.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.coco.heart.handler.PayloadHandler;
import com.google.gson.Gson;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年3月24日 下午6:39:24
 * @func
 */
public class BaseServlet extends HttpServlet {
    @Autowired
    protected PayloadHandler payloadHandler;
    private Gson gson = new Gson();

    protected void printout(Object data, HttpServletResponse response) {
        // response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Access-Control-Allow-Origin", "*"); // 允许跨域
        response.setContentType("application/json; charset=utf-8");
        response.setHeader("pragma", "no-cache");
        response.setHeader("cache-control", "no-cache");
        if (data == null) {
            data = 200;
        }
        String jsonStr = gson.toJson(data);
        try {
            PrintWriter out = response.getWriter();
            out.write(jsonStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
