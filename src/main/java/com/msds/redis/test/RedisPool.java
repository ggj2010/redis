package com.msds.redis.test;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @ClassName:RedisPool.java
 * @Description:
 * @author gaoguangjin
 * @Date 2015-5-11 下午2:45:27
 */
@Slf4j
public class RedisPool {
	
	private static JedisPoolConfig poolConfig = null;
	
	// Redis服务器IP
	private static String ADDR = "123.56.118.135";
	
	// Redis的端口号
	private static int PORT = 6379;
	
	// 访问密码
	private static String AUTH = "gaoguangjin";
	
	private static JedisPool jedisPool = null;
	
	/**
	 * 初始化Redis连接池
	 */
	static {
		try {
			poolConfig = new JedisPoolConfig();
			poolConfig.setTestOnBorrow(SysConstants.TEST_ON_BORROW);
			poolConfig.setMaxTotal(SysConstants.MAX_ACTIVE);
			poolConfig.setMaxIdle(SysConstants.MAX_IDLE);
			poolConfig.setMinIdle(SysConstants.MIN_IDLE);
			poolConfig.setMaxWaitMillis(SysConstants.MAX_WAIT);
			poolConfig.setTestWhileIdle(SysConstants.TEST_WHILE_IDLE);
			jedisPool = new JedisPool(poolConfig, ADDR, PORT, 0, AUTH);
		} catch (Exception e) {
			log.error("初始化redis连接池失败！" + e.getLocalizedMessage());
		}
	}
	
	/**
	 * 获取Jedis实例
	 * @return
	 */
	public static Jedis getJedis() {
		try {
			if (jedisPool != null) {
				Jedis resource = jedisPool.getResource();
				return resource;
			}
		} catch (Exception e) {
			log.error("获取Jedis失败！" + e.getLocalizedMessage());
		}
		return null;
	}
	
	/**
	 * 释放jedis资源
	 * @param jedis
	 */
	public static void returnResource(final Jedis jedis) {
		if (jedis != null) {
			jedisPool.returnResource(jedis);
		}
	}
}
