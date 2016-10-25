package com.coco.heart.redis;


import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.coco.heart.common.Utils;

import redis.clients.jedis.exceptions.JedisException;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年3月31日 下午10:46:37
 * @func redis 分布式锁 多台server 同时尝试向redis集群中写同一个key值，写成功者被认为成功获取到锁 key="lock_"+hostKey
 * 
 */
public class RedisLock {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisLock.class);
    private String lockKey;
    private StringRedisTemplate redisTemplate;
    private boolean fetch = false;

    public RedisLock(String lockKey, StringRedisTemplate redisTemplate) {
        this.lockKey = Utils.lockStart + lockKey;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 
     * assume that : result == true means fetch the lock.else other server fetch the lock
     *
     * @param processTime
     * @return
     */
    public boolean acquireLock() {
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        boolean result = valueOps.setIfAbsent(lockKey, "1");
        // assume that : result == true means fetch the lock.else other server fetch the lock
        if (result == true) {
            long lockLiveTime = Utils.lockKeyLivetimeInMillisecond.get();
            valueOps.set(lockKey, "2", lockLiveTime, TimeUnit.MILLISECONDS);
            LOGGER.info("fetch redis distribution_lock [{}] suceess,set lock live [{}]ms ", lockKey, lockLiveTime);
            fetch = true;
        }
        return result;
    }

    /**
     * 
     * delete the key,means release the lock
     *
     * @throws JedisException
     */
    public void releaseLock() throws JedisException {
        if (fetch == true) {
            redisTemplate.delete(lockKey);
            LOGGER.info("release redis distribution_lock [{}] suceess ", lockKey);
        }
    }
}
