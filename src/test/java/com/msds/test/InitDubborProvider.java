package com.msds.test;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

/**
 * @ClassName:InitDubborProvider.java
 * @Description: 启动dubbo服务
 * @author gaoguangjin
 * @Date 2015-5-20 下午12:56:10
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring-context.xml", "classpath:dubbo-provider.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@TestExecutionListeners(listeners = { DependencyInjectionTestExecutionListener.class, TransactionalTestExecutionListener.class })
public class InitDubborProvider {
	
	@Test
	// 发布服务
	public void provider() throws IOException {
		System.in.read(); // 为保证服务一直开着，利用输入流的阻塞来模拟
	}
	
}
