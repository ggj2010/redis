package com.msds.redis;

import redis.clients.jedis.Jedis;

import com.msds.redis.test.RedisPool;

public class Demo2 {
	private Jedis jedis;
	
	public void init() {
		// 连接redis linux服务器
		jedis = new Jedis("10.1.9.231", 6379);
		// 权限认证
		jedis.auth("msds");// 密码最好越长越好，防止暴力破解
	}
	
	public static void main(String[] args) {
		Jedis jediss = RedisPool.getJedis();
		jediss.publish("dataBase", "这是测试2");
		RedisPool.returnResource(jediss);
	}
}
