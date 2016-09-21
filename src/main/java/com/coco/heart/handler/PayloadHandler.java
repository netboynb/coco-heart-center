package com.coco.heart.handler;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.coco.heart.common.Utils;
import com.coco.heart.core.HeartCore;
import com.coco.heart.domain.HeartRequest;
import com.coco.heart.entry.PingEntry;
import com.coco.utils.Consts;
import com.coco.utils.web.Proto;
import com.coco.utils.web.Protos;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;

/**
* @author wanglin/netboy
* @version 创建时间：2016年9月21日 下午5:13:24
* @func 
*/
public class PayloadHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(PayloadHandler.class);
    private HeartCore heartCore;

    public PayloadHandler(HeartCore heartCore) {
        this.heartCore = heartCore;
    }

    public PayloadHandler() {
    }

    public Proto parsePayload(String payload) {
        Proto result = null;
        if (payload == null) {
            result = Protos.createInvalidParam("payload=null");
        } else {
            PingEntry pingEntry = payload2pingentry(payload);
            if (null == pingEntry) {
                result = Protos.createInvalidParam("zkurl serviceName groupName hostKey must has value");
            } else {
                result = Protos.OK;
                LOGGER.debug("HeartServlet,info=[{}]", pingEntry.toString());
                heartCore.processPingInfo(pingEntry);
            }
        }
        return result;
    }

    private PingEntry payload2pingentry(String payload) {
        Proto proto = Utils.gson.fromJson(payload, Proto.class);
        Map<String, String> data = (Map<String, String>) proto.getData();
        String zkurl = data.get(Consts.zkurl);
        String serviceName = data.get(Consts.serviceName);
        String groupName = data.get(Consts.groupName);
        String hostKey = data.get(Consts.hostKey);
        String liveTimeStr = data.get(Consts.liveTimeInMillisecond);
        Long liveTime = null;
        if (liveTimeStr != null) {
            liveTime = Long.valueOf(liveTimeStr);
        } else {
            liveTime = Utils.redisKeyLivetimeInMillisecond.get();
        }
        PingEntry pingEntry = new PingEntry(zkurl, serviceName, groupName, hostKey).setLiveTime(liveTime);
        if (StringUtils.isBlank(zkurl) || StringUtils.isBlank(serviceName) || StringUtils.isBlank(groupName)
                || StringUtils.isBlank(hostKey)) {
            LOGGER.error("HeartServlet, zkurl serviceName groupName hostKey must has value,info=[{}]",
                    pingEntry.toString());
            pingEntry = null;
        }
        return pingEntry;
    }

    public static HeartRequest parseRequest(FullHttpRequest request) {
        long id = Utils.idOffset.incrementAndGet();
        // get uri info
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(request.uri()).build();
        String remoteIp = uriComponents.getHost();
        String uri = uriComponents.getPath();
        String method = request.method().name();

        // get body
        ByteBuf buf = request.content();
        int readable = buf.readableBytes();
        byte[] bytes = new byte[readable];
        buf.readBytes(bytes);
        buf.clear();
        String body = new String(bytes);
        Proto proto = Utils.gson.fromJson(body, Proto.class);
        HeartRequest heartRequest = new HeartRequest(id, remoteIp, uri, method, proto);
        return heartRequest;
    }

    public List<PingEntry> getBlackList() {
        return heartCore.getBlackList();
    }

    public List<String> removeBlackList(List<String> list){
        return heartCore.removeBlackList(list);
    }

    public HeartCore getHeartCore() {
        return heartCore;
    }

    public void setHeartCore(HeartCore heartCore) {
        this.heartCore = heartCore;
    }

}
