package com.shinemo.publish.redis.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.shinemo.publish.redis.domain.RedisContext;
import com.shinemo.publish.redis.domain.RedisNode;
import com.shinemo.publish.redis.domain.RedisPoolConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.*;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RedisUtil {

    private static final Logger log = LoggerFactory.getLogger(RedisUtil.class);

    private static Map<RedisNode, JedisPool> holder = Maps.newConcurrentMap();

    public static Jedis getClient(RedisNode node) {
        JedisPool pool = holder.get(node);
        if (pool == null) {
            pool = new JedisPool(RedisPoolConfig.config, node.getIp(), node.getPort(), RedisContext.REDIS_TIMEOUT, node.getPassword(),node.getDb());
            holder.put(node, pool);
        }
        return pool.getResource();
    }

    public static void close(Jedis jedis) {
        jedis.close();
    }

    public static long incr(RedisNode node, String key, Integer time) {
        long incrRet = -1;
        Jedis jedis = null;
        try {
            jedis = getClient(node);
            long ret = jedis.incr(key);
            if (ret == 1 && time != null) {
                jedis.expire(key, time);
            }
            incrRet = ret;
        } catch (Exception e) {
            log.error("redis incr exception:{},ex:{}", key, e);
        } finally {
            // 返还到连接池
            close(jedis);
        }
        return incrRet;

    }

    public static long incrBy(RedisNode node, String key, long delt) {
        long incrRet = -1;
        Jedis jedis = null;
        try {
            jedis = getClient(node);
            long ret = jedis.incrBy(key, delt);
            incrRet = ret;
        } catch (Exception e) {
            log.error("redis incrBy exception:{},{},ex:{}", key, delt, e);
        } finally {
            // 返还到连接池
            close(jedis);
        }
        return incrRet;

    }

    /********************* k v redis start ********************************/
    /**
     * @param node  redis实例
     * @param key
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(RedisNode node, String key, Class<T> clazz) {

        String value = null;
        Jedis jedis = null;
        try {
            jedis = getClient(node);
            value = jedis.get(key);
        } catch (Exception e) {
            log.error("redis get exception:{},ex:{}", key, e);
        } finally {
            // 返还到连接池
            close(jedis);
        }
        if (clazz == String.class) return (T) value;
        return GsonUtil.fromGson2Obj(value, clazz);
    }

    public static void set(RedisNode node, String key, String value) {
        set(node, key, value, null);
    }

    public static <T> void set(RedisNode node, String key, T value) {
        set(node, key, value, null);
    }

    public static <T> void set(RedisNode node, String key, T value, Integer time) {
        String jsonValue = GsonUtil.toJson(value);
        set(node, key, jsonValue, time);
    }

    /**
     * @param nodeList
     * @param key
     * @param value
     * @param time     seconds
     */
    public static void set(RedisNode node, String key, String value, Integer time) {
        if (time == null) {
            time = -1;
        }
        Jedis jedis = null;
        try {
            jedis = getClient(node);
            jedis.set(key, value);
            if (time > 0) {
                jedis.expire(key, time);
            }
        } catch (Exception e) {
            log.error("redis set exception:{},{},ex:{}", key, value, e);
        } finally {
            // 返还到连接池
            close(jedis);
        }
    }

    public static void del(RedisNode node, String key) {
        Jedis jedis = null;
        try {
            jedis = getClient(node);
            jedis.del(key);
        } catch (Exception e) {
            log.error("redis del exception:{},ex:{}", key, e);
        } finally {
            // 返还到连接池
            close(jedis);
        }
    }

    /********************* k v redis end ********************************/

    /*********************
     * hash redis start
     ********************************/
    public static <T> void hset(RedisNode node, String namespace, String key, T value) {
        hset(node, namespace, key, GsonUtil.toJson(value));
    }

    public static void hset(RedisNode node, String namespace, String key, String value) {
        Jedis jedis = null;
        try {
            jedis = getClient(node);
            jedis.hset(namespace, key, value);
        } catch (Exception e) {
            log.error("redis hset exception:{},{},{},ex:{}", namespace, key, value, e);
        } finally {
            // 返还到连接池
            close(jedis);
        }
    }

    public static <T> T hget(RedisNode node, String namespace, String key, Class<T> clazz) {

        String value = null;
        Jedis jedis = null;
        try {
            jedis = getClient(node);
            value = jedis.hget(namespace, key);
        } catch (Exception e) {
            log.error("redis hget exception:{},{},ex:{}", namespace, key, e);
        } finally {
            // 返还到连接池
            close(jedis);
        }
        return GsonUtil.fromGson2Obj(value, clazz);

    }

    public static <T> T hget(int db, RedisNode node, String namespace, String key, Class<T> clazz) {

        String value = null;
        Jedis jedis = null;
        try {
            jedis = getClient(new RedisNode(node.getIp(), node.getPort(), node.getPassword(), db));
            value = jedis.hget(namespace, key);
        } catch (Exception e) {
            log.error("redis hget exception:{},{},ex:{}", namespace, key, e);
        } finally {
            // 返还到连接池
            close(jedis);
        }
        return GsonUtil.fromGson2Obj(value, clazz);

    }

    public static void hdel(RedisNode node, String namespace, String key) {
        Jedis jedis = null;
        try {
            jedis = getClient(node);
            jedis.hdel(namespace, key);
        } catch (Exception e) {
            log.error("redis hdel exception:{},{},ex:{}", namespace, key, e);
        } finally {
            // 返还到连接池
            close(jedis);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Map<String, T> hgetAll(RedisNode node, String namespace, Class<T> clazz) {
        Map<String, String> result = null;
        Jedis jedis = null;
        try {
            jedis = getClient(node);
            result = jedis.hgetAll(namespace);
        } catch (Exception e) {
            log.error("redis hgetAll exception:{},ex:{}", namespace, e);
        } finally {
            // 返还到连接池
            close(jedis);
        }

        if (clazz.getSimpleName().equals(String.class.getSimpleName())) {
            return (Map<String, T>) result;
        }

        if (result != null) {
            Map<String, T> newMap = Maps.newHashMap();
            Iterator<Map.Entry<String, String>> iterator = result.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                String key = entry.getKey();
                String val = entry.getValue();
                newMap.put(key, GsonUtil.fromGson2Obj(val, clazz));
            }
            return newMap;
        } else {
            return null;
        }

    }

    /**
     * 返回 key 指定的哈希集中所有字段的名字。
     *
     * @param node
     * @param key
     * @return
     */
    public static Set<String> hkeys(RedisNode node, String key) {
        Set<String> result = null;
        Jedis jedis = null;
        try {
            jedis = getClient(node);
            result = jedis.hkeys(key);
        } catch (Exception e) {
            log.error("redis hkeys exception:{},ex:{}", key, e);
        } finally {
            // 返还到连接池
            close(jedis);
        }
        return result;
    }

    /**
     * 返回 key 指定的哈希集中指定字段的值
     *
     * @param node
     * @param key
     * @param clazz
     * @param fields
     * @return
     */
    public static <T> List<T> hmget(RedisNode node, String namespace, Class<T> clazz, String... key) {

        List<String> value = null;
        Jedis jedis = null;
        try {
            jedis = getClient(node);
            value = jedis.hmget(namespace, key);
        } catch (Exception e) {
            log.error("redis hmget exception:{},{},ex:{}", namespace, key, e);
        } finally {
            // 返还到连接池
            close(jedis);
        }
        if (value != null) {
            List<T> newValue = Lists.newArrayList();
            for (String temp : value) {
                newValue.add(GsonUtil.fromGson2Obj(temp, clazz));
            }
            return newValue;
        }
        return null;

    }

    /**
     * 设置 key 指定的哈希集中指定字段的值。该命令将重写所有在哈希集中存在的字段。如果 key 指定的哈希集不存在，会创建一个新的哈希集并与 key
     * 关联
     *
     * @param nodeList
     * @param key
     * @param hash
     * @param time
     */
    public static void hmset(RedisNode node, String namespace, Map<String, String> hash, Integer time) {

        if (time == null) {
            time = -1;
        }
        Jedis jedis = null;
        try {
            jedis = getClient(node);
            jedis.hmset(namespace, hash);
            if (time > 0) {
                jedis.expire(namespace, time);
            }
        } catch (Exception e) {
            log.error("redis hmset exception:{},{},ex:{}", namespace, hash, e);
        } finally {
            // 返还到连接池
            close(jedis);
        }

    }

    public static void expire(RedisNode node, String key, Integer time) {
        if (time == null) {
            time = -1;
        }
        Jedis jedis = null;
        try {
            jedis = getClient(node);
            if (time > 0) {
                jedis.expire(key, time);
            }
        } catch (Exception e) {
            log.error("redis expire exception:{},{},ex:{}", key, e);
        } finally {
            // 返还到连接池
            close(jedis);
        }
    }

    public static void hmset(RedisNode node, String namespace, Map<String, String> hash) {
        hmset(node, namespace, hash, null);
    }

    /********************* hash redis end ********************************/

    /********************* list redis start ********************************/
    /**
     * 从队列的左边入队
     */
    public static void lpush(RedisNode node, String key, String value) {

        Jedis jedis = null;
        try {
            jedis = getClient(node);
            jedis.lpush(key, value);
        } catch (Exception e) {
            log.error("redis lpush exception:{},{},ex:{}", key, value, e);
        } finally {
            // 返还到连接池
            close(jedis);
        }

    }

    public static <T> void lpush(RedisNode node, String key, T value) {

        lpush(node, key, GsonUtil.toJson(value));

    }

    /**
     * 从队列的右边入队
     */
    public static void rpush(RedisNode node, String key, String value) {
        Jedis jedis = null;
        try {
            jedis = getClient(node);
            jedis.rpush(key, value);
        } catch (Exception e) {
            log.error("redis rpush exception:{},{},ex:{}", key, value, e);
        } finally {
            // 返还到连接池
            close(jedis);
        }
    }

    public static <T> void rpush(RedisNode node, String key, T value) {
        rpush(node, key, GsonUtil.toJson(value));
    }

    /**
     * 移除并且返回 key 对应的 list 的第一个元素
     */
    public static <T> T lpop(RedisNode node, String key, Class<T> clazz) {
        String retValue = null;
        Jedis jedis = null;
        String vaule = null;
        try {
            jedis = getClient(node);
            vaule = jedis.lpop(key);
            retValue = vaule;
        } catch (Exception e) {
            log.error("redis lpop exception:{},ex:{}", key, e);
        } finally {
            // 返还到连接池
            close(jedis);
        }
        return GsonUtil.fromGson2Obj(retValue, clazz);
    }

    /**
     * 从队列的右边出队一个元素
     */
    public static <T> T rpop(RedisNode node, String key, Class<T> clazz) {
        String retValue = null;
        Jedis jedis = null;
        String vaule = null;
        try {
            jedis = getClient(node);
            vaule = jedis.rpop(key);
            retValue = vaule;
        } catch (Exception e) {
            log.error("redis rpop exception:{},ex:{}", key, e);
        } finally {
            // 返还到连接池
            close(jedis);
        }
        return GsonUtil.fromGson2Obj(retValue, clazz);
    }

    /**
     * 从列表中获取指定返回的元素 start 和 end
     * 偏移量都是基于0的下标，即list的第一个元素下标是0（list的表头），第二个元素下标是1，以此类推。
     * 偏移量也可以是负数，表示偏移量是从list尾部开始计数。 例如， -1 表示列表的最后一个元素，-2 是倒数第二个，以此类推。
     */
    public static <T> List<T> lrange(RedisNode node, String key, int start, int end, Class<T> clazz) {
        List<String> value = null;
        Jedis jedis = null;
        try {
            jedis = getClient(node);
            value = jedis.lrange(key, start, end);
        } catch (Exception e) {
            log.error("redis lrange exception:{},{},{},ex:{}", key, start, end, e);
        } finally {
            // 返还到连接池
            close(jedis);
        }
        if (value != null) {
            List<T> newValue = Lists.newArrayList();
            for (String temp : value) {
                newValue.add(GsonUtil.fromGson2Obj(temp, clazz));
            }
            return newValue;
        }
        return null;
    }

    /**
     * 返回存储在 key 里的list的长度。 如果 key 不存在，那么就被看作是空list，并且返回长度为 0。 当存储在 key
     * 里的值不是一个list的话，会返回error。
     */
    public static long llen(RedisNode node, String key) {
        long len = 0;
        Jedis jedis = null;
        try {
            jedis = getClient(node);
            len = jedis.llen(key);
        } catch (Exception e) {
            log.error("redis llen exception:{},ex:{}", key, e);
        } finally {
            // 返还到连接池
            close(jedis);
        }
        return len;
    }

    /**
     * 移除表中所有与 value 相等的值
     *
     * @param nodeList
     * @param key
     * @param value
     */
    public static void lRem(RedisNode node, String key, String value) {

        Jedis jedis = null;
        try {
            jedis = getClient(node);
            jedis.lrem(key, 0, value);
        } catch (Exception e) {
            log.error("redis lRem exception:{},{},ex:{}", key, value, e);
        } finally {
            // 返还到连接池
            close(jedis);
        }

    }

    /********************* list redis end ********************************/

    /*********************
     * pubsub redis start
     ********************************/


    public static <T> void publish(RedisNode node, String channel, T message) {
        Jedis jedis = null;
        String value = null;
        if (message instanceof String) {
            value = (String) message;
        } else {
            value = GsonUtil.toJson(message);
        }
        try {
            jedis = getClient(node);
            jedis.publish(channel, value);
        } catch (Exception e) {
            log.error("redis publish exception:{},{},ex:{}", channel, value, e);
        } finally {
            // 返还到连接池
            close(jedis);
        }
    }


    public static void subscribe(RedisNode node, JedisPubSub pubsub, String... channel) {

        Jedis jedis = null;
        try {
            jedis = getClient(node);
            jedis.subscribe(pubsub, channel);
        } catch (Exception e) {
            log.error("redis subscribe exception:{},ex:{}", channel, e);
        } finally {
            // 返还到连接池
            close(jedis);
        }

    }

    /*********************
     * set redis start
     ********************************/
    /**
     * @param nodeList
     * @param key
     * @param value
     * @param time     seconds
     */
    public static void sAdd(RedisNode node, String key, String value) {
        Jedis jedis = null;
        try {
            jedis = getClient(node);
            jedis.sadd(key, value);
        } catch (Exception e) {
            log.error("redis sAdd exception:{},{},ex:{}", key, value, e);
        } finally {
            // 返还到连接池
            close(jedis);
        }
    }

    /**
     * @param node  返回个数
     * @param key
     * @param clazz
     * @return
     */
    public static Long sCard(RedisNode node, String key) {

        Long value = null;
        Jedis jedis = null;
        try {
            jedis = getClient(node);
            value = jedis.scard(key);
        } catch (Exception e) {
            log.error("redis sCard exception:{},ex:{}", key, e);
        } finally {
            // 返还到连接池
            close(jedis);
        }
        return value;
    }

    public static void sRem(RedisNode node, String key, String value) {

        Jedis jedis = null;
        try {
            jedis = getClient(node);
            jedis.srem(key, value);
        } catch (Exception e) {
            log.error("redis sRem exception:{},{},ex:{}", key, value, e);
        } finally {
            // 返还到连接池
            close(jedis);
        }

    }

    /**
     * 默认使用每页10个
     *
     * @param node
     * @param key
     * @param clazz
     * @param fields
     * @return
     */
    public static <T> List<T> sScan(RedisNode node, String key, Class<T> clazz, int start) {

        List<String> value = null;
        Jedis jedis = null;
        try {
            jedis = getClient(node);
            ScanResult<String> sscanResult = jedis.sscan(key, start + "", new ScanParams().count(10));
            if (sscanResult != null && sscanResult.getResult() != null) {
                value = sscanResult.getResult();
            }
        } catch (Exception e) {
            log.error("redis sScan exception:{},{},ex:{}", key, start, e);
        } finally {
            // 返还到连接池
            close(jedis);
        }
        if (value != null) {
            List<T> newValue = Lists.newArrayList();
            for (String temp : value) {
                newValue.add(GsonUtil.fromGson2Obj(temp, clazz));
            }
            return newValue;
        }
        return null;

    }

    /*********************
     * sorted set
     ********************************/
    /**
     * @param nodeList
     * @param key
     * @param value
     * @param time     seconds
     */
    public static void zAdd(RedisNode node, String key, String value) {
        Jedis jedis = null;
        try {
            jedis = getClient(node);
            jedis.zadd(key, 0, value);
        } catch (Exception e) {
            log.error("redis zAdd exception:{},{},ex:{}", key, value, e);
        } finally {
            // 返还到连接池
            close(jedis);
        }
    }

    /**
     * @param node  返回个数
     * @param key
     * @param clazz
     * @return
     */
    public static Long zCard(RedisNode node, String key) {

        Long value = null;
        Jedis jedis = null;
        try {
            jedis = getClient(node);
            value = jedis.zcard(key);
        } catch (Exception e) {
            log.error("redis zCard exception:{},ex:{}", key, e);
        } finally {
            // 返还到连接池
            close(jedis);
        }
        return value;
    }

    public static void zRem(RedisNode node, String key, String value) {

        Jedis jedis = null;
        try {
            jedis = getClient(node);
            jedis.zrem(key, value);
        } catch (Exception e) {
            log.error("redis zRem exception:{},{},ex:{}", key, value, e);
        } finally {
            // 返还到连接池
            close(jedis);
        }

    }

    /**
     * 从列表中获取指定返回的元素 start 和 end
     * 偏移量都是基于0的下标，即list的第一个元素下标是0（list的表头），第二个元素下标是1，以此类推。
     * 偏移量也可以是负数，表示偏移量是从list尾部开始计数。 例如， -1 表示列表的最后一个元素，-2 是倒数第二个，以此类推。
     */
    public static <T> List<T> zrange(RedisNode node, String key, int start, int end, Class<T> clazz) {
        Set<String> value = null;
        Jedis jedis = null;
        try {
            jedis = getClient(node);
            value = jedis.zrange(key, start, end);
        } catch (Exception e) {
            log.error("redis zrange exception:{},{},{},ex:{}", key, start, end, e);
        } finally {
            // 返还到连接池
            close(jedis);
        }
        if (value != null) {
            List<T> newValue = Lists.newArrayList();
            for (String temp : value) {
                newValue.add(GsonUtil.fromGson2Obj(temp, clazz));
            }
            return newValue;
        }
        return null;
    }

    public static boolean exist(RedisNode node, String key) {

        Jedis jedis = null;
        try {
            jedis = getClient(node);
            return jedis.exists(key);
        } catch (Exception e) {
            log.error("redis exist exception:{},ex:{}", key, e);
        } finally {
            // 返还到连接池
            close(jedis);
        }
        return false;

    }


}
