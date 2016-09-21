package com.coco.heart.redis.listener;

import java.util.Map;

import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.coco.heart.entry.PingEntry;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年4月1日 下午1:23:50
 * @func
 */
public interface CarkeyRedisListener extends MessageListener {
    void setBlackHostsMap(Map<String, PingEntry> blackHostMap);

    void setStringRedisTemplate(StringRedisTemplate template);
}
