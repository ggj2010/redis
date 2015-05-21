package com.msds.redis.test;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPubSub;

@Slf4j
public class NotifyDataBase extends JedisPubSub {
	
	@Override
	public void onMessage(String channel, String sql) {
		log.info("redis更新转换成数据库==》sql:" + sql);
		
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
