package com.msds.redis.util;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import com.msds.redis.dao.RedisDao;
import com.msds.redis.dao.RedisUpdateToDataBase;

/**
 * @ClassName:NotifyDataBase.java
 * @Description: pub/sub 异步调用类
 * @author gaoguangjin
 * @Date 2015-5-22 上午10:13:29
 */
@Slf4j
@Service("notifyDataBase")
public class NotifyDataBase extends JedisPubSub {
	@Autowired
	RedisUpdateToDataBase redisUpdateToDataBase;
	
	@Autowired
	RedisCacheManager redisCacheManager;
	
	@Override
	public void onMessage(String channel, String sql) {
		log.info("redis更新转换成数据库==》sql:" + sql);
		
		RedisCachePool pool = redisCacheManager.getRedisPoolMap().get(RedisDataBaseType.defaultType.toString());
		final Jedis jedis = pool.getResource();
		
		Long length = jedis.llen(RedisDao.LOG);
		int n = 2;// 如果log的list size 达到n 的时候就一次性执行更新。测试的时候就弄成2
		List<String> list = new ArrayList<String>();
		if (length >= n) {
			for (int i = 0; i < n; i++) {
				String sqlStr = jedis.lpop(RedisDao.LOG);// 删除list首元素
				list.add(sqlStr);
			}
			
			// 是否执行成功
			boolean flag = redisUpdateToDataBase.excuteUpdate(list);
			if (!flag) {
				for (String oldSql : list) {
					jedis.lpush(RedisDao.LOG, oldSql);// 更新失败重新添加到list里面
				}
			}
			
		}
		
	}
	
	@Override
	public void onPMessage(String pattern, String channel, String message) {
		log.info("onPMessage");
	}
	
	@Override
	public void onSubscribe(String channel, int subscribedChannels) {
		log.info("开始监控redis变化！");
		
	}
	
	@Override
	public void onUnsubscribe(String channel, int subscribedChannels) {
		log.info("onUnsubscribe");
		
	}
	
	@Override
	public void onPUnsubscribe(String pattern, int subscribedChannels) {
		log.info("onPUnsubscribe");
		
	}
	
	@Override
	public void onPSubscribe(String pattern, int subscribedChannels) {
		log.info("onPSubscribe");
	}
	
}
