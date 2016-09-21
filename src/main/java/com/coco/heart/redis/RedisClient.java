package com.coco.heart.redis;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.management.RuntimeErrorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.coco.heart.entry.PingEntry;
import com.coco.heart.redis.listener.CarkeyRedisListener;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年3月31日 下午6:26:32
 * @func
 */
public class RedisClient implements RedisClientInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisClient.class);
    private StringRedisTemplate redisTemplte;
    private CarkeyRedisListener expiredRedisListener;
    private CarkeyRedisListener subRedisListener;

    private Thread notifyThread;
    /*
     * notify-keyspace-events
     * 
     * key-space notification : __keyspace@0__:mykey expired
     * 
     * key-event notification : __keyevent@*__:expired mykey
     *
     */
    private String notifyPattern = "__keyevent@*__:expired";


    public RedisClient(StringRedisTemplate redisTemplte, CarkeyRedisListener expiredRedisListener,
            CarkeyRedisListener subRedisListener, String notifyPattern) {
        super();
        this.redisTemplte = redisTemplte;
        this.expiredRedisListener = expiredRedisListener;
        this.subRedisListener = subRedisListener;
        this.notifyPattern = notifyPattern;
    }

    public RedisClient(StringRedisTemplate redisTemplte, CarkeyRedisListener expiredRedisListener,
            CarkeyRedisListener subRedisListener, Thread notifyThread, String notifyPattern) {
        super();
        this.redisTemplte = redisTemplte;
        this.expiredRedisListener = expiredRedisListener;
        this.subRedisListener = subRedisListener;
        this.notifyThread = notifyThread;
        this.notifyPattern = notifyPattern;
    }

    public RedisClient() {
        super();
    }

    public void start() {
        check();
        expiredRedisListener.setStringRedisTemplate(redisTemplte);
        // new one thread to watch the redis expire event
        if (notifyThread == null) {
            notifyThread = new Thread(new Runnable() {
                public void run() {
                    redisTemplte.execute(new RedisCallback<Boolean>() {
                        @Override
                        public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                            connection.pSubscribe(expiredRedisListener, notifyPattern.getBytes());
                            return true;
                        }
                    });
                }
            });
        }
        notifyThread.setDaemon(true);
        String threadName = "Expired-notify-fetch-Thread";
        notifyThread.setName(threadName);
        notifyThread.start();
        LOGGER.info("thread [{}] started", threadName);
    }

    public void check() {
        if (null == redisTemplte || null == expiredRedisListener || subRedisListener == null) {
            throw new RuntimeErrorException(null,
                    "redisTemplte || expiredRedisListener || subRedisListener can't be null,please init them");
        }
    }

    @Override
    public void close() {}

    @Override
    public void setBlackHostsMap(Map<String, PingEntry> blackHostMap) {
        // expiredRedisListener.setBlackHostsMap(blackHostMap);
        subRedisListener.setBlackHostsMap(blackHostMap);
    }

    @Override
    public void set(String key, String value) {
        ValueOperations<String, String> valueOps = redisTemplte.opsForValue();
        valueOps.set(key, value);
    }

    @Override
    public void set(String key, String value, TimeUnit timeUnit, Long expiredTime) {
        ValueOperations<String, String> valueOps = redisTemplte.opsForValue();
        valueOps.set(key, value, expiredTime, timeUnit);
    }

    @Override
    public Object get(String key) {
        ValueOperations<String, String> valueOps = redisTemplte.opsForValue();

        return valueOps.get(key);
    }

    @Override
    public void del(String key) {
        redisTemplte.delete(key);
    }

    @Override
    public void expire(String key, TimeUnit timeUnit, long timeout) {
        redisTemplte.expire(key, timeout, timeUnit);
    }

    @Override
    public boolean exists(String key) {
        return redisTemplte.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.exists(key.getBytes());
            }
        });
    }

    @Override
    public long increment(String key, Long value) {
        ValueOperations<String, String> valueOps = redisTemplte.opsForValue();
        return valueOps.increment(key, value);
    }

    public StringRedisTemplate getRedisTemplte() {
        return redisTemplte;
    }

    public void setRedisTemplte(StringRedisTemplate redisTemplte) {
        this.redisTemplte = redisTemplte;
    }


    public Thread getNotifyThread() {
        return notifyThread;
    }

    public void setNotifyThread(Thread notifyThread) {
        this.notifyThread = notifyThread;
    }

    public String getNotifyPattern() {
        return notifyPattern;
    }

    public void setNotifyPattern(String notifyPattern) {
        this.notifyPattern = notifyPattern;
    }

    public CarkeyRedisListener getExpiredRedisListener() {
        return expiredRedisListener;
    }

    public void setExpiredRedisListener(CarkeyRedisListener expiredRedisListener) {
        this.expiredRedisListener = expiredRedisListener;
    }

    public CarkeyRedisListener getSubRedisListener() {
        return subRedisListener;
    }

    public void setSubRedisListener(CarkeyRedisListener subRedisListener) {
        this.subRedisListener = subRedisListener;
    }

}
