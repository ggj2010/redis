package com.msds.test;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @ClassName:InitDubborProvider.java
 * @Description: 启动dubbo服务
 * @author gaoguangjin
 * @Date 2015-5-20 下午12:56:10
 */
@Slf4j
public class InitDubborProvider {
	// @Before
	@Test
	// 发布服务
	public void provider() throws IOException {
		
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "dubbo-provider.xml" });
		context.start();
		// System.in.read(); // 为保证服务一直开着，利用输入流的阻塞来模拟
		// ApplicationContext ac = new ClassPathXmlApplicationContext("dubbo/dubbo-provider.xml");
	}
	
	// @Test//调用服务
	// public void test() {
	// ApplicationContext context = new ClassPathXmlApplicationContext("dubbo/dubbo-consumer.xml");
	// // context.start();
	// DemoService demoService = (DemoService) context.getBean("demoService"); // 获取远程服务代理
	// String hello = demoService.sayHello("world"); // 执行远程方法
	// log.info(hello + "============================================");
	//
	// }
}
