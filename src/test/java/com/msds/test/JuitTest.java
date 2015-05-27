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
import com.msds.dubbo.service.NoteService;
import com.msds.redis.util.NotifyDataBase;
import com.msds.redis.util.RedisCacheManager;
import com.msds.redis.util.RedisCachePool;
import com.msds.redis.util.RedisDataBaseType;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring-context.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@TestExecutionListeners(listeners = { DependencyInjectionTestExecutionListener.class, TransactionalTestExecutionListener.class })
/**
 * @ClassName:JuitTest.java
 * @Description:   redis测试 
 * @author gaoguangjin
 * @Date 2015-5-21 上午10:03:00
 */
public class JuitTest {
	@Autowired
	RedisCacheManager redisCacheManager;
	@Qualifier("NoteServiceImp")
	@Autowired
	BaseService baseService;
	@Autowired
	NoteService noteService;
	
	// 启动日志监听，用MonitorSql 类代替 因为单元测试里面多线程无法堵塞
	// @Before
	public void before() {
		RedisCachePool pool = redisCacheManager.getRedisPoolMap().get(RedisDataBaseType.defaultType.toString());
		final Jedis jedis = pool.getResource();
		final JedisPubSub ndb = new NotifyDataBase();
		new Thread() {
			public void run() {// 会广播形式打印log日志
				jedis.subscribe(ndb, "publog");
			}
		}.start();
	}
	
	// 查询所有数据。redis和服务器子同一局域网下
	// @Test
	public void findAll() {
		long time = System.currentTimeMillis();
		List<Note> list = baseService.findAll();
		
		for (Note note : list) {
			log.info(note.toString());
		}
		long time2 = System.currentTimeMillis();
		log.info("耗时" + (time2 - time));// 9790
	}
	
	// 查询单条数据
	// @Test
	public void findOne() {
		String id = "1";
		Note note = noteService.queryById(id);
		log.info(note.toString());
	}
	
	@Test
	public void insert() {
		Note note = new Note();
		note.setFlag(0);
		note.setFromUrl("www.ggjlovezjy.com:1314");
		note.setNoteName("测试插入");
		note.setAuthorName("高广金测试插入");
		baseService.insert(note);
	}
	
	// 查询带参数的
	// @Test
	public void findByParam() {
		Note note = new Note();
		note.setAuthorName("张静月");
		note.setFromUrl("http://www.tuicool.com/");
		List<Note> noteList = noteService.queryParamAnd(note);
		
		for (Note list : noteList) {
			log.info(list.toString());
		}
	}
	
	/**
	 * @Description: 测试删除
	 */
	@Test
	public void delete() {
		for (int i = 0; i < 2; i++) {
			baseService.delete(i + "");
		}
	}
	
	/**
	 * @Description: 测试更新。更新需要注意的细节就是，先从redis里面查询出来的值，然后在上面做修改。
	 */
	// @Test
	public void update() {
		String id = "50";
		Note note = noteService.queryById(id);
		note.setAuthorName("张静月");
		note.setFromUrl("www.ggjlovezjy.com:1314");
		baseService.update(note);
	}
	
	// @Test
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
