package com.coco.heart.redis.listener;

import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.coco.heart.common.ThreadPoolHolder;
import com.coco.heart.common.Utils;
import com.coco.heart.core.BizTask;
import com.coco.heart.entry.PingEntry;
import com.coco.heart.redis.RedisLock;
import com.coco.heart.redis.pubsub.PubRedisService;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年3月31日 下午9:46:37
 * @func
 * 
 *       notify-keyspace-events
 * 
 *       key-space notification : __keyspace@0__:mykey expired
 * 
 *       key-event notification : __keyevent@0__:expired mykey
 *
 */
public class RedisExpireListener implements CarkeyRedisListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisExpireListener.class);
    private StringRedisTemplate redisTemplate;
    private ExecutorService executorService;
    private Integer threadPoolSizeMin = 4;
    private Integer threadPoolSizeMax = 8;
    private PubRedisService pubRedisService;


    public RedisExpireListener(StringRedisTemplate redisTemplate, ExecutorService executorService) {
        super();
        this.redisTemplate = redisTemplate;
        this.executorService = executorService;
    }

    public RedisExpireListener() {
        super();
    }

    public void init() {
        if (null == executorService) {
            String poolName = "Expired-Notify-Deal-Pool";
            executorService = ThreadPoolHolder.getFixedPool(poolName, threadPoolSizeMin, threadPoolSizeMax);
            LOGGER.info("thread pool  [{}] init with min={},max ={}", poolName, threadPoolSizeMin, threadPoolSizeMax);
        }
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        if (null == executorService) {
            init();
        }
        // foo
        String key = new String(message.getBody());

        // here need to sure the expired key's name?
        // __keyevent@0__:expired
        String channel = new String(message.getChannel());
        String[] array = channel.split(":");
        String eventType = null;
        if (array.length > 1) {
            eventType = array[1];
        }

        if (StringUtils.isEmpty(eventType) || !eventType.equals("expired")) {
            LOGGER.info("[{}] isn't we sub,ignore it,info={}", eventType, channel);
            return;
        }
        // deal with the lock expired event
        if (!key.startsWith(Utils.pingKeyPrefix)) {
            LOGGER.error("it is not one soa ping expire event,ignore it ,the key={}", key);
            return;
        }
        // deal with the ping key/value expired event
        RedisLock channelLock = new RedisLock(key, redisTemplate);
        boolean isFetchLock = false;
        try {
            if (channelLock.acquireLock()) {
                isFetchLock = true;
                long traceId = redisTemplate.opsForValue().increment(Utils.traceIdKey, 1);
                // do expire logic
                PingEntry pingEntry = Utils.redisKey2PingEntry(key);
                pingEntry.setTraceId(traceId);
                // do the biz logic in thread pool
                ThreadPoolHolder.getFixedPool(Utils.BIZ_THREAD_POOL_NAME, threadPoolSizeMin, threadPoolSizeMax)
                        .submit(new BizTask(pingEntry, traceId, pubRedisService));
                LOGGER.error("[ trace_id ]=[ {} ]  deal [{}] expiredEvent={},channel={} ", traceId, key, eventType,
                        channel);
            } else {
                LOGGER.error("[ RedisExpireListener ]  event:{},channel:{} are handling by other server", eventType,
                        channel);
            }
        } catch (Exception ex) {
            LOGGER.error("[ RedisExpireListener ]  deal redis expire event error,event:{},channel:{},exception:",
                    eventType, channel, ex);
        } finally {
            try {
                if (isFetchLock) {
                    channelLock.releaseLock();
                }
            } catch (Exception e) {
                LOGGER.error(
                        "[ RedisExpireListener]  release channel lock failed when deal redis expire event,event:{},channel:{},exception:",
                        eventType, channel, e);
            }
        }
    }

    public StringRedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    public ExecutorService getExecutorService() {
        return executorService;
    }


    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public void setBlackHostsMap(Map<String, PingEntry> blackHostMap) {}

    @Override
    public void setStringRedisTemplate(StringRedisTemplate template) {
        this.redisTemplate = template;
    }

    public Integer getThreadPoolSizeMin() {
        return threadPoolSizeMin;
    }

    public void setThreadPoolSizeMin(Integer threadPoolSizeMin) {
        this.threadPoolSizeMin = threadPoolSizeMin;
    }

    public Integer getThreadPoolSizeMax() {
        return threadPoolSizeMax;
    }

    public void setThreadPoolSizeMax(Integer threadPoolSizeMax) {
        this.threadPoolSizeMax = threadPoolSizeMax;
    }

    public PubRedisService getPubRedisService() {
        return pubRedisService;
    }

    public void setPubRedisService(PubRedisService pubRedisService) {
        this.pubRedisService = pubRedisService;
    }

}
