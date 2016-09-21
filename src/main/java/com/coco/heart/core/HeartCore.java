package com.coco.heart.core;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.management.RuntimeErrorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import com.coco.heart.common.ThreadPoolHolder;
import com.coco.heart.common.Utils;
import com.coco.heart.entry.PingEntry;
import com.coco.heart.redis.RedisClient;
import com.coco.heart.redis.pubsub.PubRedisService;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年3月25日 上午11:44:05
 * @func
 */
public class HeartCore {
    private static final Logger LOGGER = LoggerFactory.getLogger(HeartCore.class);
    private Map<String, PingEntry> blackHostMap = Maps.newConcurrentMap();
    private RedisClient redisClient;
    private PubRedisService pubRedisService;
    private RedisMessageListenerContainer ListenerContainer;

    public void start() {
        check();
        redisClient.setBlackHostsMap(blackHostMap);
        redisClient.start();
        ListenerContainer.start();
    }

    public void close() {
        redisClient.close();
        blackHostMap.clear();
    }

    private void check() {
        if (null == redisClient || pubRedisService == null) {
            throw new RuntimeErrorException(null, "redisClient || pubRedisService can't be null ,please init them");
        }
    }

    /**
     * 
     * TODO: load pingEntry info into check list
     *
     * @param pingEntry
     */
    public void processPingInfo(PingEntry pingEntry) {
        String hostkey = pingEntry.getHostKey();
        if (blackHostMap.containsKey(hostkey)) {
            // 1 remove from local black map
            blackHostMap.remove(hostkey);
            // 2 publish the hostkey to other subers
            pubRedisService.pub(Joiner.on(Utils.pubSubKey).join(Utils.hostLive, hostkey));
            // 3 modify the status on the zookeeper
            // get the dead host's ping ,it must be recover,so it needs to be avaliable on zookeeper
            // opt zookeeper go into alone thread
            ThreadPoolHolder.getFixedPool(Utils.BIZ_THREAD_POOL_NAME, 4, 200)
                    .submit(new OptRegisterTask(pingEntry, true));
            LOGGER.info("remove from blackSet, into liveHostsMap,host=[{}]", pingEntry.toString());
        }
        Long temp = pingEntry.getTimeStamp();
        Long timestamp = temp == null ? System.currentTimeMillis() : temp;
        Long liveTime = pingEntry.getLiveTime();
        redisClient.set(pingEntry.toKey(), timestamp + "", TimeUnit.MILLISECONDS, liveTime);
        LOGGER.info("[{}] write to redis, timestamp={},liveTime={}", pingEntry.toString(), timestamp, liveTime);
    }

    /**
     * 
     * TODO: fetch the black hosts list
     *
     * @return
     */
    public List<PingEntry> getBlackList() {
        List<PingEntry> blackList = ImmutableList.copyOf(blackHostMap.values());
        return blackList;
    }

    /**
     * 
     * TODO: remove the specify list,then fetch the result list
     *
     * @param removeList
     * @return
     */
    public List<String> removeBlackList(List<String> removeList) {
        List<String> resultList = Collections.emptyList();
        removeList.forEach(item -> {
            if (blackHostMap.containsKey(item)) {
                blackHostMap.remove(item);
                resultList.add(item);
            }
        });
        return resultList;
    }

    public void addBlackList(List<PingEntry> list) {
        list.forEach(item -> {
            String key = item.getHostKey();
            if (!blackHostMap.containsKey(item)) {
                blackHostMap.put(key, item);
            }
        });
        return;
    }

    public Map<String, PingEntry> getBlackHostMap() {
        return blackHostMap;
    }

    public void setBlackHostMap(Map<String, PingEntry> blackHostMap) {
        this.blackHostMap = blackHostMap;
    }

    public RedisClient getRedisClient() {
        return redisClient;
    }

    public void setRedisClient(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    public PubRedisService getPubRedisService() {
        return pubRedisService;
    }

    public void setPubRedisService(PubRedisService pubRedisService) {
        this.pubRedisService = pubRedisService;
    }

    public RedisMessageListenerContainer getListenerContainer() {
        return ListenerContainer;
    }

    public void setListenerContainer(RedisMessageListenerContainer listenerContainer) {
        ListenerContainer = listenerContainer;
    }

}
