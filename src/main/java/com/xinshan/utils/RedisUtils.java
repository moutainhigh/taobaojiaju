package com.xinshan.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;

/**
 * Created by jonson.xu on 15-4-23.
 */
public class RedisUtils {
    private static JedisPool jedisPool = null;

    public static JedisPool getPool() {
        if (jedisPool == null) {
            JedisPoolConfig config = new JedisPoolConfig();
            // 控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
            // 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
            config.setMaxIdle(500);
            // 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
            config.setMaxIdle(5);
            // 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
            config.setMaxWaitMillis(1000 * 10);
            // 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
            config.setTestOnBorrow(true);
            jedisPool = new JedisPool(config, CommonUtils.REDIS_HOST, Integer.parseInt(CommonUtils.REDIS_PORT));
        }
        return jedisPool;
    }

    public static Long rpush(String key, String value) {
        Jedis jedis = getPool().getResource();
        try {
            return jedis.rpush(key, value);
        } catch (Exception ex) {
            ex.printStackTrace();
            getPool().returnBrokenResource(jedis);
        } finally {
            getPool().returnResource(jedis);
        }
        return null;
    }

    public static String hget(String key, String field) {
        Jedis jedis = getPool().getResource();
        String result = "";
        try {
            return jedis.hget(key, field);
        } catch (Exception ex) {
            ex.printStackTrace();
            getPool().returnBrokenResource(jedis);
        } finally {
            getPool().returnResource(jedis);
        }
        return result;
    }

    public static List<String> lrange(String key, long start, long end) {
        Jedis jedis = getPool().getResource();
        try {
            return jedis.lrange(key, start, end);
        } catch (Exception ex) {
            ex.printStackTrace();
            getPool().returnBrokenResource(jedis);
        } finally {
            getPool().returnResource(jedis);
        }
        return null;
    }

    public static Long del(String key) {
        Jedis jedis = getPool().getResource();
        try {
            return jedis.del(key);
        } catch (Exception ex) {
            ex.printStackTrace();
            getPool().returnBrokenResource(jedis);
        } finally {
            getPool().returnResource(jedis);
        }
        return null;
    }

    public static String get(String key) {
        Jedis jedis = getPool().getResource();
        try {
            return jedis.get(key);
        } catch (Exception ex) {
            ex.printStackTrace();
            getPool().returnBrokenResource(jedis);
        } finally {
            getPool().returnResource(jedis);
        }
        return null;
    }

    public static String set(String key, Object value) {
        Jedis jedis = getPool().getResource();
        try {
            return jedis.set(key, value.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            getPool().returnBrokenResource(jedis);
        } finally {
            getPool().returnResource(jedis);
        }
        return null;
    }

    public static Long hset(String key, String field, Object value) {
        Jedis jedis = getPool().getResource();
        try {
            return jedis.hset(key, field, value.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            getPool().returnBrokenResource(jedis);
        } finally {
            getPool().returnResource(jedis);
        }
        return null;
    }
}
