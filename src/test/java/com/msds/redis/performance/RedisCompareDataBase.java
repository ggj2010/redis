package com.msds.redis.performance;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
import org.springframework.transaction.annotation.Transactional;

import redis.clients.jedis.Jedis;

import com.msds.dubbo.bean.Note;
import com.msds.dubbo.service.BaseService;
import com.msds.dubbo.service.NoteService;
import com.msds.redis.util.RedisCacheManager;
import com.msds.redis.util.RedisCachePool;
import com.msds.redis.util.RedisDataBaseType;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring-context.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@TestExecutionListeners(listeners = { DependencyInjectionTestExecutionListener.class,
		TransactionalTestExecutionListener.class })
public class RedisCompareDataBase {

	private static Logger log = Logger.getLogger(RedisCompareDataBase.class);

	@Autowired
	SessionFactory sessionFactory;
	@Autowired
	RedisCacheManager redisCacheManager;

	@Qualifier("NoteServiceImp")
	@Autowired
	BaseService baseService;

	@Autowired
	NoteService noteService;

	// @Test
	// @Transactional
	public void selectAll() {
		long time1 = System.currentTimeMillis();
		Query query = sessionFactory.getCurrentSession().createQuery("from Note");
		List<Note> objectList = query.list();
		for (Note note : objectList) {
			log.info(note.toString());
		}
		long time2 = System.currentTimeMillis();

		List<Note> noteList = baseService.findAll();
		for (Note note : objectList) {
			log.info(note.toString());
		}
		long time3 = System.currentTimeMillis();

		log.info("jdbc查询  数据大小" + noteList.size() + "耗时：" + (time2 - time1));
		log.info("redis查询 数据大小" + objectList.size() + " 耗时：" + (time3 - time2));

	}

	// @Test
	// @Transactional
	public void selectOne() {
		long time1 = System.currentTimeMillis();
		Session session = sessionFactory.getCurrentSession();
		for (int i = 1; i < 100; i++) {
			Note note = (Note) session.get(Note.class, i);
			log.info(note.toString());
		}
		long time2 = System.currentTimeMillis();

		for (int i = 1; i < 100; i++) {
			Note note2 = noteService.queryById(i + "");
			log.info(note2.toString());
		}
		long time3 = System.currentTimeMillis();

		log.info("jdbc查询  据单条数据耗时：" + (time2 - time1));
		log.info("redis查询 单条数据 耗时：" + (time3 - time2));
	}

	// redis查询单条数据的某个字段值，循环100是用来放大倍数的 jdbc查询 单条数据的某个字段值耗时：375||redis查询
	// 单条数据的某个字段值 耗时：207
	// @Test
	// @Transactional
	public void selectParam() {
		long time1 = System.currentTimeMillis();
		Session session = sessionFactory.getCurrentSession();
		for (int i = 1; i < 100; i++) {
			Note note = (Note) session.get(Note.class, i);
			log.info(note.getNoteName());
		}
		long time2 = System.currentTimeMillis();

		RedisCachePool pool = redisCacheManager.getRedisPoolMap().get(RedisDataBaseType.defaultType.toString());
		Jedis jedis = pool.getResource();

		for (int i = 1; i < 100; i++) {
			log.info(jedis.get("Note:" + i + ":noteName"));
		}
		long time3 = System.currentTimeMillis();

		log.info("jdbc查询  单条数据的某个字段值耗时：" + (time2 - time1));
		log.info("redis查询 单条数据的某个字段值 耗时：" + (time3 - time2));
	}

	// 1706 //1815
	@Test
	@Transactional
	public void update() {
		long time1 = System.currentTimeMillis();
		Session session = sessionFactory.getCurrentSession();
		Note note = (Note) session.get(Note.class, 1);
		if (null != note) {
			note.setAuthorName("张静月1");
			note.setFromUrl("www.ggjlovezjy.com:13141");
		}
		session.save(note);
		long time2 = System.currentTimeMillis();

		RedisCachePool pool = redisCacheManager.getRedisPoolMap().get(RedisDataBaseType.defaultType.toString());
		Jedis jedis = pool.getResource();
		Note note2 = noteService.queryById("1");
		if (null != note2) {
			note.setAuthorName("张静月2");
			note.setFromUrl("www.ggjlovezjy.com:13142");
		}
		baseService.update(note2);
		long time3 = System.currentTimeMillis();

		log.info("jdbc 更新数据的某个字段值耗时：" + (time2 - time1));
		log.info("redis 更新数据的某个字段值 耗时：" + (time3 - time2));
	}
}
