package com.msds.test;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import com.msds.redis.test.NotifyDataBase;
import com.msds.redis.test.RedisPool;

/**
 * @ClassName:MonitorSql.java
 * @Description: 监控对redis的更新和删除
 * @author gaoguangjin
 * @Date 2015-5-21 上午10:32:23
 */
@Slf4j
public class MonitorSql {
	public static void main(String[] args) {
		final Jedis jedis = RedisPool.getJedis();
		final JedisPubSub ndb = new NotifyDataBase();
		new Thread() {
			public void run() {// 会广播形式打印log日志
				jedis.subscribe(ndb, "publog");
			}
		}.start();
	}
}
