package com.msds.redis.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @ClassName:RedisCachePool.java
 * @Description:
 * @author gaoguangjin
 * @Date 2015-5-25 下午2:30:33
 */
public class RedisCachePool {
	
	private JedisPool jedisPool = null;
	private Integer db;
	
	public RedisCachePool(Integer db, JedisPool jedisPool) {
		this.db = db;
		this.jedisPool = jedisPool;
		
	}
	
	public Jedis getResource() {
		Jedis jedisInstance = null;
		if (jedisPool != null) {
			jedisInstance = jedisPool.getResource();
			if (db > 0) {// 每次获得连接之前，切换到对应的数据库。默认的是0
				jedisInstance.select(db);
			}
		}
		return jedisInstance;
	}
	
	/**
	 * 释放jedis资源
	 * @param jedis
	 */
	public void returnResource(final Jedis jedis) {
		if (jedis != null) {
			jedisPool.returnResource(jedis);
		}
	}
}
