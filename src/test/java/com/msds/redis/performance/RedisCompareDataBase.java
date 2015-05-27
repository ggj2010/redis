package com.msds.redis.performance;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.hibernate.Query;
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

import com.msds.dubbo.bean.Note;
import com.msds.dubbo.service.BaseService;
import com.msds.redis.util.RedisCacheManager;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring-context.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@TestExecutionListeners(listeners = { DependencyInjectionTestExecutionListener.class, TransactionalTestExecutionListener.class })
public class RedisCompareDataBase {
	
	@Autowired
	SessionFactory sessionFactory;
	@Autowired
	RedisCacheManager redisCacheManager;
	
	@Qualifier("NoteServiceImp")
	@Autowired
	BaseService baseService;
	
	@Test
	public void select() {
		long time1 = System.currentTimeMillis();
		Query query = sessionFactory.getCurrentSession().createQuery("from Note");
		List<Note> objectList = query.list();
		for (Note note : objectList) {
			log.info(note.toString());
		}
		
		List<Note> noteList = baseService.findAll();
		for (Note note : objectList) {
			log.info(note.toString());
		}
		
	}
	
}
