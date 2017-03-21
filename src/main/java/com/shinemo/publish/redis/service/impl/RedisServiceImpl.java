package com.shinemo.publish.redis.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.JedisPubSub;

import com.shinemo.publish.redis.domain.RedisNode;
import com.shinemo.publish.redis.service.RedisService;
import com.shinemo.publish.redis.util.RedisUtil;

public class RedisServiceImpl implements RedisService {

    private RedisNode node;

    public RedisServiceImpl(RedisNode node) {
        this.node = node;
    }

    @Override
    public long incr(String key, Integer time) {
        return RedisUtil.incr(node, key, time);
    }

    @Override
    public long incrBy(String key, long delt) {
        return RedisUtil.incrBy(node, key, delt);
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        return RedisUtil.get(node, key, clazz);
    }

    @Override
    public void set(String key, String value) {
        RedisUtil.set(node, key, value);
    }

    @Override
    public <T> void set(String key, T value) {
        RedisUtil.set(node, key, value);
    }

    @Override
    public <T> void set(String key, T value, Integer time) {
        RedisUtil.set(node, key, value, time);
    }

    @Override
    public void set(String key, String value, Integer time) {
        RedisUtil.set(node, key, value, time);
    }

    @Override
    public void del(String key) {
        RedisUtil.del(node, key);
    }

    @Override
    public <T> void hset(String namespace, String key, T value) {
        RedisUtil.hset(node, namespace, key, value);
    }

    @Override
    public void hset(String namespace, String key, String value) {
        RedisUtil.hset(node, namespace, key, value);
    }

    @Override
    public <T> T hget(String namespace, String key, Class<T> clazz) {
        return RedisUtil.hget(node, namespace, key, clazz);
    }

    @Override
    public <T> T hget(int db, String namespace, String key, Class<T> clazz) {
        return RedisUtil.hget(db, node, namespace, key, clazz);
    }

    @Override
    public void hdel(String namespace, String key) {
        RedisUtil.hdel(node, namespace, key);
    }

    @Override
    public <T> Map<String, T> hgetAll(String namespace, Class<T> clazz) {
        return RedisUtil.hgetAll(node, namespace, clazz);
    }

    @Override
    public Set<String> hkeys(String key) {
        return RedisUtil.hkeys(node, key);
    }

    @Override
    public <T> List<T> hmget(String namespace, Class<T> clazz, String... key) {
        return RedisUtil.hmget(node, namespace, clazz, key);
    }

    @Override
    public void hmset(String namespace, Map<String, String> hash, Integer time) {
        RedisUtil.hmset(node, namespace, hash, time);
    }

    @Override
    public void expire(String key, Integer time) {
        RedisUtil.expire(node, key, time);
    }

    @Override
    public void hmset(String namespace, Map<String, String> hash) {
        RedisUtil.hmset(node, namespace, hash);
    }

    @Override
    public void lpush(String key, String value) {
        RedisUtil.lpush(node, key, value);
    }

    @Override
    public <T> void lpush(String key, T value) {
        RedisUtil.lpush(node, key, value);
    }

    @Override
    public void rpush(String key, String value) {
        RedisUtil.rpush(node, key, value);
    }

    @Override
    public <T> void rpush(String key, T value) {
        RedisUtil.rpush(node, key, value);
    }

    @Override
    public <T> T lpop(String key, Class<T> clazz) {
        return RedisUtil.lpop(node, key, clazz);
    }

    @Override
    public <T> T rpop(String key, Class<T> clazz) {
        return RedisUtil.rpop(node, key, clazz);
    }

    @Override
    public <T> List<T> lrange(String key, int start, int end, Class<T> clazz) {
        return RedisUtil.lrange(node, key, start, end, clazz);
    }

    @Override
    public long llen(String key) {
        return RedisUtil.llen(node, key);
    }

    @Override
    public void lRem(String key, String value) {
        RedisUtil.lRem(node, key, value);
    }

    @Override
    public <T> void publish(String channel, T message) {
        RedisUtil.publish(node, channel, message);
    }

    @Override
    public void subscribe(JedisPubSub pubsub, String... channel) {
        RedisUtil.subscribe(node, pubsub, channel);
    }

    @Override
    public void sAdd(String key, String value) {
        RedisUtil.sAdd(node, key, value);
    }

    @Override
    public Long sCard(String key) {
        return RedisUtil.sCard(node, key);
    }

    @Override
    public void sRem(String key, String value) {
        RedisUtil.sRem(node, key, value);
    }

    @Override
    public <T> List<T> sScan(String key, Class<T> clazz, int start) {
        return RedisUtil.sScan(node, key, clazz, start);
    }

    @Override
    public void zAdd(String key, String value) {
        RedisUtil.zAdd(node, key, value);
    }

    @Override
    public Long zCard(String key) {
        return RedisUtil.zCard(node, key);
    }

    @Override
    public void zRem(String key, String value) {
        RedisUtil.zRem(node, key, value);
    }

    @Override
    public <T> List<T> zrange(String key, int start, int end, Class<T> clazz) {
        return RedisUtil.zrange(node, key, start, end, clazz);
    }

    @Override
    public boolean exist(String key) {
        return RedisUtil.exist(node, key);
    }


}
