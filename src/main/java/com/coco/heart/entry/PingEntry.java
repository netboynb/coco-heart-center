package com.coco.heart.entry;

import com.coco.heart.common.Utils;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年3月25日 上午11:34:06
 * @func
 */
public class PingEntry extends BaseEntry {
    private String zkurl;
    private String serviceName;
    private String groupName;
    private String hostKey;// ip:port
    private Long timeStamp;
    private Long traceId = -1L;
    private Long liveTime;// in millisecond

    public PingEntry() {
        super();
    }


    public PingEntry(String zkurl, String serviceName, String groupName, String hostKey, Long traceId) {
        super();
        this.zkurl = zkurl;
        this.serviceName = serviceName;
        this.groupName = groupName;
        this.hostKey = hostKey;
        this.traceId = traceId;
    }

    public PingEntry(String zkurl, String serviceName, String groupName, String hostKey) {
        this(zkurl, serviceName, groupName, hostKey, -1L);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(hostKey);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PingEntry) {
            PingEntry that = (PingEntry) obj;
            return Objects.equal(hostKey, that.hostKey);
        }
        return false;
    }

    public String getZkurl() {
        return zkurl;
    }

    public PingEntry setZkurl(String zkurl) {
        this.zkurl = zkurl;
        return this;
    }

    public String getServiceName() {
        return serviceName;
    }

    public PingEntry setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public String getGroupName() {
        return groupName;
    }

    public PingEntry setGroupName(String groupName) {
        this.groupName = groupName;
        return this;
    }

    public String getHostKey() {
        return hostKey;
    }

    public PingEntry setHostKey(String hostKey) {
        this.hostKey = hostKey;
        return this;
    }

    public Long getTraceId() {
        return traceId;
    }

    public void setTraceId(Long traceId) {
        this.traceId = traceId;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String toKey() {
        return Joiner.on("&").join(new Object[] {Utils.pingKeyPrefix, zkurl, serviceName, groupName, hostKey});
    }

    public String toKeyWithTraceId() {
        return Joiner.on("&").join(new Object[] {zkurl, serviceName, groupName, hostKey, traceId});
    }


    public Long getLiveTime() {
        return liveTime;
    }


    public PingEntry setLiveTime(Long liveTime) {
        this.liveTime = liveTime;
        return this;
    }

}
