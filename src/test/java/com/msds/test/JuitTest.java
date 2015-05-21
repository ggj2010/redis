package com.msds.test;

import java.util.List;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import com.msds.dubbo.bean.Note;
import com.msds.dubbo.service.BaseService;
import com.msds.redis.test.NotifyDataBase;
import com.msds.redis.util.RedisCacheManager;
import com.msds.redis.util.RedisCachePool;
import com.msds.redis.util.RedisDataBaseType;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring-context.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@TestExecutionListeners(listeners = { DependencyInjectionTestExecutionListener.class, TransactionalTestExecutionListener.class })
public class JuitTest {
	@Autowired
	RedisCacheManager redisCacheManager;
	@Qualifier("NoteServiceImp")
	@Autowired
	BaseService NoteService;
	
	// 启动日志监听
	// @Test
	public void before() {
		RedisCachePool pool = redisCacheManager.getRedisPoolMap().get(RedisDataBaseType.defaultType.toString());
		final Jedis jedis = pool.getResource();
		
		new Thread() {
			public void run() {// 会广播形式打印log日志
				JedisPubSub ndb = new NotifyDataBase();
				jedis.subscribe(ndb, "publog");
			}
		}.start();
		
		// display(jedis);
		
		pool.returnResource(jedis);
		
	}
	
	// @Test
	public void find() {
		List<Note> list = NoteService.findAll();
		for (Note note : list) {
			log.info(note.toString());
		}
	}
	
	@Test
	public void delete() {
		NoteService.delete("2");
	}
	
	@Test
	public void after() {
		RedisCachePool pool = redisCacheManager.getRedisPoolMap().get(RedisDataBaseType.defaultType.toString());
		Jedis jedis = pool.getResource();
		log.info("======删除之后打印===========");
		display(jedis);
		pool.returnResource(jedis);
	}
	
	private void display(Jedis jedis) {
		Set<String> aa = jedis.smembers("Note:createdate:2015-05-20 01:04:13.0");
		Set<String> bb = jedis.smembers("Note:fromUrl:http://www.tuicool.com/articles/vquaei");
		Set<String> cc = jedis.smembers("Note:flag:0");
		Set<String> dd = jedis.smembers("Note:authorName:高广金");
		for (String string1 : aa) {
			log.info("验证a" + string1);// 日期有重复的
		}
		for (String string2 : bb) {
			log.info("验证b" + string2);
		}
		for (String string3 : cc) {
			log.info("验证c" + string3);
		}
		for (String string4 : dd) {
			log.info("验证d" + string4);
		}
		
	}
}
