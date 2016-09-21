package com.coco.heart.redis.pubsub;

import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年4月4日 上午10:04:35
 * @func
 */
public class PubRedisService {
    private StringRedisTemplate stringRedisTemplate;
    private String channelTopic = "modifyZk:topic";

    /* 发布消息到Channel */
    public void pub(String message) {
        stringRedisTemplate.convertAndSend(channelTopic, message);
    }

    public PubRedisService(StringRedisTemplate stringRedisTemplate, String channelTopic) {
        super();
        this.stringRedisTemplate = stringRedisTemplate;
        this.channelTopic = channelTopic;
    }

    public PubRedisService() {
        super();
    }

    public StringRedisTemplate getStringRedisTemplate() {
        return stringRedisTemplate;
    }

    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public String getChannelTopic() {
        return channelTopic;
    }

    public void setChannelTopic(String channelTopic) {
        this.channelTopic = channelTopic;
    }

}
