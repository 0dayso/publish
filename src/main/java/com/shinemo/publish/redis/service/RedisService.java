package com.shinemo.publish.redis.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.JedisPubSub;

public interface RedisService {

	public long incr(String key, Integer time);

	public long incrBy(String key, long delt);

	public <T> T get(String key, Class<T> clazz);

	public void set(String key, String value);

	public <T> void set(String key, T value);

	public <T> void set(String key, T value, Integer time);

	public void set(String key, String value, Integer time);

	public void del(String key);

	public <T> void hset(String namespace, String key, T value);

	public void hset(String namespace, String key, String value);

	public <T> T hget(String namespace, String key, Class<T> clazz);

	public <T> T hget(int db,String namespace, String key, Class<T> clazz);

	public void hdel(String namespace, String key);

	public <T> Map<String, T> hgetAll(String namespace, Class<T> clazz);

	public Set<String> hkeys(String key);

	public <T> List<T> hmget(String namespace, Class<T> clazz, String... key);

	public void hmset(String namespace, Map<String, String> hash, Integer time);

	public void hmset(String namespace, Map<String, String> hash);

	public void lpush(String key, String value);

	public <T> void lpush(String key, T value);

	/**
	 * 从队列的右边入队
	 */
	public void rpush(String key, String value);

	public <T> void rpush(String key, T value);

	/**
	 * 移除并且返回 key 对应的 list 的第一个元素
	 */
	public <T> T lpop(String key, Class<T> clazz);

	/**
	 * 从队列的右边出队一个元素
	 */
	public <T> T rpop(String key, Class<T> clazz);

	public <T> List<T> lrange(String key, int start, int end, Class<T> clazz);

	/**
	 * 返回存储在 key 里的list的长度。 如果 key 不存在，那么就被看作是空list，并且返回长度为 0。 当存储在 key
	 * 里的值不是一个list的话，会返回error。
	 */
	public long llen(String key);

	/**
	 * 移除表中所有与 value 相等的值
	 * 
	 * @param nodeList
	 * @param key
	 * @param value
	 */
	public void lRem(String key, String value);

	/********************* list redis end ********************************/

	/*********************
	 * pubsub redis start
	 ********************************/

	public <T> void publish(String channel, T message);

	public void subscribe(JedisPubSub pubsub, String... channel);

	/*********************
	 * set redis start
	 ********************************/
	/**
	 * @param nodeList
	 * @param key
	 * @param value
	 * @param time
	 *            seconds
	 */
	public void sAdd(String key, String value);

	/**
	 * @param node
	 *            返回个数
	 * @param key
	 * @param clazz
	 * @return
	 */
	public Long sCard(String key);

	public void sRem(String key, String value);

	/**
	 * 默认使用每页10个
	 *
	 * @param node
	 * @param key
	 * @param clazz
	 * @param fields
	 * @return
	 */
	public <T> List<T> sScan(String key, Class<T> clazz, int start);

	/*********************
	 * sorted set
	 ********************************/
	/**
	 * @param nodeList
	 * @param key
	 * @param value
	 * @param time
	 *            seconds
	 */
	public void zAdd(String key, String value);

	/**
	 * @param node
	 *            返回个数
	 * @param key
	 * @param clazz
	 * @return
	 */
	public Long zCard(String key);

	public void zRem(String key, String value);

	/**
	 * 从列表中获取指定返回的元素 start 和 end
	 * 偏移量都是基于0的下标，即list的第一个元素下标是0（list的表头），第二个元素下标是1，以此类推。
	 * 偏移量也可以是负数，表示偏移量是从list尾部开始计数。 例如， -1 表示列表的最后一个元素，-2 是倒数第二个，以此类推。
	 */
	public <T> List<T> zrange(String key, int start, int end, Class<T> clazz);

	public void expire(String key, Integer time);
	
	public boolean exist(String key);

}
