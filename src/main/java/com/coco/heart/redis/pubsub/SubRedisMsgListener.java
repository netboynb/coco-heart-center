package com.coco.heart.redis.pubsub;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import com.coco.heart.common.Utils;
import com.coco.heart.entry.PingEntry;
import com.coco.heart.redis.listener.CarkeyRedisListener;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年4月4日 上午9:57:26
 * @func
 */
public class SubRedisMsgListener implements CarkeyRedisListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubRedisMsgListener.class);
    private ChannelTopic channelTopic;
    private Map<String, PingEntry> blackHostMap;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String msgInfo = new String(message.getBody());
        String chnnelInfo = new String(message.getChannel());
        String[] array = msgInfo.trim().split(Utils.pubSubKey);
        String infoType = array[0];
        String data = array[1];
        switch (infoType) {
            case "dead":
                PingEntry pingEntry = Utils.redisKeyWithTraceId2PingEntry(data);
                blackHostMap.put(pingEntry.getHostKey(), pingEntry);
                LOGGER.info("traceId=[{}] PubSub host=[{}] into  blackMap,msgInfo={}", pingEntry.getTraceId(),
                        pingEntry.getHostKey(), msgInfo);
                break;
            case "live":
                if (blackHostMap.containsKey(data)) {
                    PingEntry pingEntry2 = blackHostMap.remove(data);
                    LOGGER.info("traceId=[{}] PubSub chnnelInfo=[{}],msg=[{}],remove from blackMap",
                            pingEntry2.getTraceId(), chnnelInfo, msgInfo);
                } else {
                    LOGGER.info(" PubSub chnnelInfo=[{}],msg=[{}],not in the blackMap", chnnelInfo, msgInfo);
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void setStringRedisTemplate(StringRedisTemplate template) {}

    @Override
    public void setBlackHostsMap(Map<String, PingEntry> blackHostMap) {
        this.blackHostMap = blackHostMap;
    }

    public ChannelTopic getChannelTopic() {
        return channelTopic;
    }

    public void setChannelTopic(ChannelTopic channelTopic) {
        this.channelTopic = channelTopic;
    }

    public Map<String, PingEntry> getBlackHostMap() {
        return blackHostMap;
    }

    public void setBlackHostMap(Map<String, PingEntry> blackHostMap) {
        this.blackHostMap = blackHostMap;
    }

}
