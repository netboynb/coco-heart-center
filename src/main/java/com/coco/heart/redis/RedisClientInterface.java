package com.coco.heart.redis;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.coco.heart.entry.PingEntry;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年3月31日 下午5:29:05
 * @func
 */
public interface RedisClientInterface {
    void start();

    void set(String key, String value);

    void set(String key, String value, TimeUnit timeUnit, Long expiredTime);

    Object get(String key);

    void del(String key);

    void expire(String key, TimeUnit timeUnit, long timeout);

    boolean exists(String key);

    void setBlackHostsMap(Map<String, PingEntry> blackHostMap);

    long increment(String key, Long value);

    void close();
}
