package com.msds.test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.transaction.Transactional;

import lombok.extern.slf4j.Slf4j;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.msds.dubbo.bean.Note;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring-context.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@TestExecutionListeners(listeners = { DependencyInjectionTestExecutionListener.class, TransactionalTestExecutionListener.class })
/**
 * @ClassName:InitDataToDataBase.java
 * @Description: 初始化网站数据到数据库
 * @author gaoguangjin
 * @Date 2015-5-21 上午9:55:48
 */
public class InitDataToDataBase extends AbstractTransactionalJUnit4SpringContextTests {
	@Autowired
	private SessionFactory sessionFactory;
	
	// 存放url 和title
	public ConcurrentHashMap<String, String> currentHashMap = new ConcurrentHashMap<String, String>();
	
	@Test
	@Transactional
	public void test() throws IOException {
		final Session session = sessionFactory.getCurrentSession();
		ExecutorService pool = Executors.newFixedThreadPool(10);
		try {
			for (int i = 0; i < 20; i++) {// 总共20也 开启20个线程去爬去链接
				pool.execute(new PutArticelUrlByPage(currentHashMap, i));
			}
			pool.shutdown();
			pool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);// 线程池里面线程所有执行完之后执行下面
			
			// 处理map里面的链接，保存到数据库。大概有1123个url
			System.out.println("map大小：" + currentHashMap.size());
			long beginTime = System.currentTimeMillis();
			
			// 【2】多线程插入、CountDownLatch是用来解决防止session关闭的问题
			CountDownLatch latch = new CountDownLatch(currentHashMap.size());
			List<Note> listNote = new StoreToDataBaseByThread(currentHashMap, session, latch).insertToDatabase();
			latch.await();
			// 因为用的是durid连接池，获取数据库连接和创建jdbc 必须要在一个线程里面。所以采用串行保存
			for (Note note : listNote) {
				session.save(note);
			}
			
			long endTime = System.currentTimeMillis();
			log.info("插入数据库耗时：" + (endTime - beginTime) + "ms");
			
			System.out.println("end");
		} catch (InterruptedException e) {
			log.error("" + e.getLocalizedMessage());
		}
	}
}
